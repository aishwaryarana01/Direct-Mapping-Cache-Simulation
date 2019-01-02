public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Direct Mapping Simulation 1 with n : 24, L : 16 and M : 16");
        DirectMappingCacheSimulator directMappingCacheSimulator = new DirectMappingCacheSimulator(24, 16, 16, "Trace.txt", false);
        directMappingCacheSimulator.Simulate();

        System.out.println("");
        System.out.println("Direct Mapping Simulation 2 with n : 24, L : 16 and M : 32");
        DirectMappingCacheSimulator directMappingCacheSimulator1 = new DirectMappingCacheSimulator(24, 16, 32, "Trace.txt", false);
        directMappingCacheSimulator1.Simulate();
    }
}