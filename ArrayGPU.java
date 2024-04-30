package main;

import org.jocl.*;

import java.util.Arrays;
import java.util.HashMap;

import static org.jocl.CL.*;

public class ArrayGPU {
    public static String subSource = "__kernel void " +
            "sampleKernel(__global const float *a," +
            "             __global const float *b," +
            "             __global float *c)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    float x = (a[gid * 3 + 0] + b[4]);" +
            "    float y = (a[gid * 3 + 1] + b[5]);" +
            "    float z = (a[gid * 3 + 2] + b[6]);" +
            "    c[gid * 3 + 0] = acos((x * b[0] + y * b[1] + z * b[2]) / (sqrt(x * x + y * y + z * z) * b[3]));" +
            "    float con = 2.0 * (b[7] * x + b[8] * y + b[9] * z);" +
            "    c[gid * 3 + 1] = b[8] * con + y * b[11] + (b[9] * x - b[7] * z) * b[10] * 2.0;" +
            "    c[gid * 3 + 2] = b[9] * con + z * b[11] + (b[7] * y - b[8] * x) * b[10] * 2.0;" +
            "}";

    public cl_context context;
    public cl_kernel kernel;
    public cl_command_queue commandQueue;
    public HashMap<Integer, cl_mem[]> memObjects = new HashMap<>();
    public cl_program program;

    public ArrayGPU(){
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL.CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        this.context = CL.clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        this.commandQueue =
                CL.clCreateCommandQueue(this.context, device, 0, null);
    }

    public void startProgram(String programSource){
        this.program = CL.clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        CL.clBuildProgram(this.program, 0, null, null, null, null);

        // Create the kernel
        this.kernel = CL.clCreateKernel(this.program, "sampleKernel", null);
        clReleaseProgram(program);
    }

    public void allocateMemory(int n, float[] srcArrayA, int b, int hash){
        // Allocate the memory objects for the input and output data
        Pointer srcA = Pointer.to(srcArrayA);
        cl_mem[] mem = new cl_mem[3];
        mem[0] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * n, srcA, null);
        mem[1] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY,
                (long) Sizeof.cl_float * b, null, null);
        mem[2] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_WRITE,
                (long) Sizeof.cl_float * n, null, null);
        memObjects.put(hash, mem);
    }

    public float[] runProgram(int n, float[] srcArrayB, int ids, int hash) {
        float[] dstArray = new float[n];
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);

        cl_mem[] mem = memObjects.get(hash);
        if(mem == null){
            return dstArray;
        }

        clEnqueueWriteBuffer(commandQueue, mem[1], CL_TRUE, 0,
                (long) 12 * Sizeof.cl_float, srcB, 0, null, null);

        // Set the arguments for the kernel
        CL.clSetKernelArg(this.kernel, 0,
                Sizeof.cl_mem, Pointer.to(mem[0]));
        CL.clSetKernelArg(this.kernel, 1,
                Sizeof.cl_mem, Pointer.to(mem[1]));
        CL.clSetKernelArg(this.kernel, 2,
                Sizeof.cl_mem, Pointer.to(mem[2]));

        // Set the work-item dimensions
        long[] global_work_size = new long[]{ids};

        // Execute the kernel
        CL.clEnqueueNDRangeKernel(this.commandQueue, this.kernel, 1, null,
                global_work_size, null, 0, null, null);

        // Read the output data
        CL.clEnqueueReadBuffer(this.commandQueue, mem[2], CL_TRUE, 0,
                (long) n * Sizeof.cl_float, dst, 0, null, null);

        return dstArray;
    }

    public void closeGPU(){
        // Release kernel, program, and memory objects
        for (cl_mem[] mem: memObjects.values()) {
            CL.clReleaseMemObject(mem[0]);
            CL.clReleaseMemObject(mem[1]);
            CL.clReleaseMemObject(mem[2]);
        }
        CL.clReleaseContext(this.context);
        CL.clReleaseCommandQueue(this.commandQueue);
        CL.clReleaseKernel(this.kernel);
        clReleaseProgram(this.program);
    }

    private static String getString(cl_device_id device, int paramName) {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        CL.clGetDeviceInfo(device, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        CL.clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }
}