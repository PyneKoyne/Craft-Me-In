// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: ArrayGPU creates an object which can interface with the GPU to perform an array of tasks simultaneously

package main;

// imports
import org.jocl.*;
import java.util.HashMap;
import static org.jocl.CL.*;

// ArrayGPU class to create a new ArrayGPU object. Heavily inspired by JOCL template code
public class ArrayGPU {
    // openCL code to find where a point projects onto the scren
    public static String projectionSource = "__kernel void " +
            "sampleKernel(__global const float *a," +
            "             __global const float *b," +
            "             __global float *c)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    float x = (a[gid * 3 + 0] - b[12]);" +
            "    float y = (a[gid * 3 + 1] - b[13]);" +
            "    float z = (a[gid * 3 + 2] - b[14]);" +
            "    float angle = acos((x * b[0] + y * b[1] + z * b[2]) / (sqrt(x * x + y * y + z * z) * b[3]));" +
            "    if (angle < 1.57079632679) {" +
            "       float con = 2.0 * (b[4] * x + b[5] * y + b[6] * z);" +
            "       float new_y = b[5] * con + y * b[8] + (b[6] * x - b[4] * z) * b[7] * 2.0;" +
            "       float new_z = b[6] * con + z * b[8] + (b[4] * y - b[5] * x) * b[7] * 2.0;" +
            "       float hyp = (angle * b[9] * 8192)/sqrt(new_y * new_y + new_z * new_z);" +
            "       c[gid * 2] = new_y * hyp + b[10];" +
            "       c[gid * 2 + 1] = new_z * hyp + b[11];" +
            "    }" +
            "    else {" +
            "       c[gid * 2] = 0;" +
            "       c[gid * 2 + 1] = 0;" +
            "    }" +
            "}";

    // variables for the gpu
    private cl_context context;
    private cl_kernel kernel;
    private cl_command_queue commandQueue;
    private HashMap<Integer, cl_mem[]> memObjects = new HashMap<>();
    private cl_program program;
    private cl_mem camMem;

    // constructor which sets all the default information for the GPU
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

    // Creates a program based on the source code
    public void startProgram(String programSource){
        this.program = CL.clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        CL.clBuildProgram(this.program, 0, null, null, null, null);

        // Create the kernel
        this.kernel = CL.clCreateKernel(this.program, "sampleKernel", null);
        clReleaseProgram(program);
        this.camMem = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY,
                (long) Sizeof.cl_float * 15, null, null);
    }

    // allocates memory objects to save time if they are used between seperate runs
    public void allocateMemory(int n, float[] srcArrayA, int hash){
        // Allocate the memory objects for the input and output data
        Pointer srcA = Pointer.to(srcArrayA);
        cl_mem[] mem = new cl_mem[3];
        mem[0] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * n, srcA, null);
        mem[2] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_WRITE,
                (long) Sizeof.cl_float * n, null, null);
        memObjects.put(hash, mem);
    }

    // removes memory objects when deloaded
    public void unallocateMemory(int hash){
        // Allocate the memory objects for the input and output data
        if (memObjects.containsKey(hash)) {
            cl_mem[] mem = memObjects.get(hash);
            CL.clReleaseMemObject(mem[0]);
            CL.clReleaseMemObject(mem[2]);
            memObjects.remove(hash);
        }
    }

    // sets the memory data of the camera details
    public void setCamMem(float[] srcArrayB){
        Pointer srcB = Pointer.to(srcArrayB);

        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 0,
                (long) 12 * Sizeof.cl_float, srcB, 0, null, null);
    }

    // Executes the program based on the input received and the other stored memory objects
    public float[] runProgram(int n, float[] focal, int ids, int hash) {
        float[] dstArray = new float[n];
        Pointer srcB = Pointer.to(focal);
        Pointer dst = Pointer.to(dstArray);

        cl_mem[] mem = this.memObjects.get(hash);
        if(mem == null){
            return dstArray;
        }
        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 12 * Sizeof.cl_float,
                (long) 3 * Sizeof.cl_float, srcB, 0, null, null);

        // Set the arguments for the kernel
        CL.clSetKernelArg(this.kernel, 0,
                Sizeof.cl_mem, Pointer.to(mem[0]));
        CL.clSetKernelArg(this.kernel, 1,
                Sizeof.cl_mem, Pointer.to(this.camMem));
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

    // closes the GPU and releases all memory objects
    public void closeGPU(){
        // Release kernel, program, and memory objects
        CL.clReleaseMemObject(camMem);
        for (cl_mem[] mem: memObjects.values()) {
            CL.clReleaseMemObject(mem[0]);
            CL.clReleaseMemObject(mem[2]);
        }
        CL.clReleaseContext(this.context);
        CL.clReleaseCommandQueue(this.commandQueue);
        CL.clReleaseKernel(this.kernel);
        clReleaseProgram(this.program);
    }
}