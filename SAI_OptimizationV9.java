import java.util.*;

public class SAI_OptimizationV9 {

    // List to store all valid solutions
    private static List<double[]> validSolutions = new ArrayList<>();

    // Main optimization function
    public static void optimizeInjection(double targetCooling, double maxOzoneDepletion, double totalInjectionLimit, double simulationYears) {
        
        // Define the range for latitude, longitude, altitude, and injection rate
        int[] latitudeRange = {-60, 60}; // Expanded latitude range to cover a broader area
        int[] longitudeRange = {-180, 180};
        int[] altitudeRange = {15, 25}; // Altitude remains the same (15-25 km)
        double[] injectionRange = {0, totalInjectionLimit};

        // Variable to store the best solution
        double[] bestSolution = null;

        // Start the recursive optimization
        bestSolution = recursiveOptimize(latitudeRange, longitudeRange, altitudeRange, injectionRange, simulationYears, targetCooling, maxOzoneDepletion, bestSolution);

        // Output all valid solutions found
        System.out.println("All valid solutions:");
        for (double[] solution : validSolutions) {
            System.out.printf("Latitude = %.2f, Longitude = %.2f, Altitude = %.2f, Injection Rate = %.2f Tg/year, Cumulative Cooling = %.2f°C, Cumulative Ozone Impact = %.2f%%\n",
                    solution[0], solution[1], solution[2], solution[3], solution[4], solution[5]);
        }

        // Output the best solution found (if any)
        if (bestSolution != null) {
            System.out.println("\nBest Solution:");
            System.out.println("\nBase Temperature = 15.2");
            System.out.printf("Latitude = %.2f, Longitude = %.2f, Altitude = %.2f, Injection Rate = %.2f Tg/year, Cumulative Cooling = %.2f°C, Cumulative Ozone Impact = %.2f%%\n",
                    bestSolution[0], bestSolution[1], bestSolution[2], bestSolution[3], bestSolution[4], bestSolution[5]);
        } else {
            System.out.println("No valid solution found.");
        }
    }

    // Recursive optimization method
    public static double[] recursiveOptimize(int[] latitudeRange, int[] longitudeRange, int[] altitudeRange, double[] injectionRange, 
                                             double yearsLeft, double targetCooling, double maxOzoneDepletion, double[] bestSolution) {
        // Base case: If no years left or no valid injection range, stop recursion
        if (yearsLeft <= 0 || injectionRange[1] <= 0) {
            return bestSolution;
        }

        // Divide the ranges into sub-parts (midpoint calculation)
        int midLatitude = (latitudeRange[0] + latitudeRange[1]) / 2;
        int midLongitude = (longitudeRange[0] + longitudeRange[1]) / 2;
        int midAltitude = (altitudeRange[0] + altitudeRange[1]) / 2;
        double midInjectionRate = (injectionRange[0] + injectionRange[1]) / 2;

        // Initialize a large value for ozone impact (representing the worst case)
        double minOzoneImpact = Double.POSITIVE_INFINITY;

        // Iterate through the possible values for each parameter (latitude, longitude, altitude, injection rate)
        for (int lat : new int[]{latitudeRange[0], midLatitude, latitudeRange[1]}) {
            for (int lon : new int[]{longitudeRange[0], midLongitude, longitudeRange[1]}) {
                for (int alt : new int[]{altitudeRange[0], midAltitude, altitudeRange[1]}) {
                    for (double injectRate = injectionRange[0]; injectRate <= injectionRange[1]; injectRate += 0.05) { // Reduced step size

                        // Simulate the injection process and get the cooling and ozone impact
                        double[] results = simulateInjection(lat, lon, alt, injectRate);
                        double cooling = results[0];
                        double ozoneImpact = results[1];

                        // If the solution meets the criteria, store it in the valid solutions list
                        if (cooling >= targetCooling && ozoneImpact <= maxOzoneDepletion) {
                            double[] currentSolution = {lat, lon, alt, injectRate, cooling, ozoneImpact};
                            validSolutions.add(currentSolution); // Add to the list of valid solutions

                            // Update the best solution if the ozone impact is minimized
                            if (ozoneImpact < minOzoneImpact) {
                                minOzoneImpact = ozoneImpact;
                                bestSolution = currentSolution;
                            }
                        }
                    }
                }
            }
        }

        // Refine by reducing ranges and continue searching recursively
        return recursiveOptimize(
            new int[]{latitudeRange[0], midLatitude}, new int[]{longitudeRange[0], midLongitude}, 
            new int[]{altitudeRange[0], midAltitude}, new double[]{injectionRange[0], midInjectionRate}, 
            yearsLeft - 1, targetCooling, maxOzoneDepletion, bestSolution
        );
    }

    // Simulate the injection process and calculate cooling and ozone impact
    public static double[] simulateInjection(int latitude, int longitude, int altitude, double injectionRate) {
        final double baseTemperature = 15.2; // Base temperature in Celsius
        final double baseOzoneLevel = 300.0; // Base ozone level (arbitrary units)
        
        // Adjust global cooling factor for realistic values
        final double globalCoolingFactor = 0.1; // Adjusted factor for realistic cooling per Tg/year

        // Adjust the ozone depletion factor to reflect more realistic impact
        final double ozoneDepletionFactor = 0.05; // Adjusted factor for realistic ozone depletion

        Map<String, Double> conditions = getAtmosphericConditions(latitude, longitude, altitude);
        double aerosolSpreadRate = calculateAerosolSpreadRate(conditions, injectionRate);

        // Calculate the cooling effect in Celsius
        double temperatureReduction = calculateCoolingEffect(aerosolSpreadRate, globalCoolingFactor);
        double cumulativeCooling = temperatureReduction * injectionRate; // Cumulative cooling over simulation years

        // Calculate ozone depletion in percentage
        double ozoneImpact = calculateOzoneDepletion(aerosolSpreadRate, ozoneDepletionFactor);
        double cumulativeOzoneImpact = ozoneImpact * injectionRate; // Cumulative ozone impact over simulation years

        return new double[]{cumulativeCooling, cumulativeOzoneImpact};
    }

    // Calculate the ozone depletion
    public static double calculateOzoneDepletion(double aerosolSpreadRate, double ozoneDepletionFactor) {
        return aerosolSpreadRate * ozoneDepletionFactor;
    }

    // Convert effect value to percentage based on the baseline
    public static double convertToPercentage(double effectValue, double baselineValue) {
        return (effectValue / baselineValue) * 100;
    }

    // Calculate the cooling effect in Celsius
    public static double calculateCoolingEffect(double aerosolSpreadRate, double globalCoolingFactor) {
        return aerosolSpreadRate * globalCoolingFactor;
    }

    // Calculate the aerosol spread rate based on atmospheric conditions and injection rate
    public static double calculateAerosolSpreadRate(Map<String, Double> atmosphericConditions, double injectionRate) {
        double windSpeed = atmosphericConditions.get("wind_speed");
        return injectionRate * (1 + windSpeed / 100);
    }

    // Generate random atmospheric conditions based on latitude, longitude, and altitude
    public static Map<String, Double> getAtmosphericConditions(double latitude, double longitude, double altitudeKm) {
        Random random = new Random(42); // Fixed seed for consistent random values
        
        // Create a map to store the atmospheric conditions
        Map<String, Double> conditions = new HashMap<>();
        
        // Generate consistent values for each condition
        double pressure = 100 + (200 * random.nextDouble());  // hPa, range between 100 and 300
        double temperature = -73 + (20 * random.nextDouble());  // Celsius, range between -73 and -53
        double humidity = random.nextDouble();  // Relative humidity, range between 0 and 1
        double windSpeed = 10 + (20 * random.nextDouble());  // m/s, range between 10 and 30

        // Put values into the map
        conditions.put("pressure", pressure);
        conditions.put("temperature", temperature);
        conditions.put("humidity", humidity);
        conditions.put("wind_speed", windSpeed);

        return conditions;
    }

    public static void main(String[] args) {
        // Input the desired parameters
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the desired cooling in terms of °C: ");
        double targetCooling = scanner.nextDouble();

        System.out.print("Enter the maximum ozone depletion acceptable in terms of percentage: ");
        double maxOzoneDepletion = scanner.nextDouble();

        System.out.print("Enter the total injection limit in terms of Tg/year: ");
        double totalInjectionLimit = scanner.nextDouble();

        System.out.print("Enter the number of years for this simulation: ");
        double simulationYears = scanner.nextDouble();

        // Start the optimization process
        optimizeInjection(targetCooling, maxOzoneDepletion, totalInjectionLimit, simulationYears);
    }
}