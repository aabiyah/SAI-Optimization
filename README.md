# SAI Optimization

**SAI Optimization** is a Java-based simulation algorithm designed to optimize the parameters of Stratospheric Aerosol Injection (SAI) for geoengineering purposes. The algorithm aims to find the best combination of latitude, longitude, altitude, and injection rate to achieve desired cooling while minimizing ozone depletion over a specified simulation period.

## Overview

Stratospheric Aerosol Injection (SAI) is a proposed method to mitigate global warming by injecting aerosols into the stratosphere, where they reflect sunlight and reduce global temperatures. However, SAI can have unintended side effects, such as ozone layer depletion.

This program uses a recursive optimization algorithm to simulate and identify valid SAI configurations that:
1. Achieve a target cooling effect (°C).
2. Keep ozone depletion within acceptable limits (%).
3. Stay within the specified total injection limit (Tg/year).

---

## Features

- **Recursive Optimization:** Efficiently narrows down the search space by dividing ranges and refining solutions.
- **Simulated Injection Effects:** Simulates cooling and ozone depletion based on atmospheric conditions.
- **Customizable Parameters:** Users can specify target cooling, ozone depletion thresholds, injection limits, and simulation duration.
- **Valid Solutions Tracking:** Stores and displays all valid solutions found during the simulation.

---

## Algorithm Details

### Input Parameters
The algorithm accepts the following user inputs:
1. **Target Cooling (°C):** The desired reduction in global temperature.
2. **Max Ozone Depletion (%):** The maximum allowable ozone depletion.
3. **Total Injection Limit (Tg/year):** The total amount of aerosols that can be injected annually.
4. **Simulation Years:** The number of years for the simulation.

### Optimization Process
1. **Recursive Search:**
   - Divides latitude, longitude, altitude, and injection rate ranges into smaller sub-ranges.
   - Evaluates each combination of parameters.
2. **Simulation:**
   - Simulates the effects of aerosol injection on cooling and ozone depletion.
   - Atmospheric conditions (e.g., wind speed, temperature) are generated randomly based on location.
3. **Validation:**
   - Checks if the simulated cooling meets or exceeds the target cooling.
   - Ensures the ozone depletion is within the acceptable threshold.
4. **Best Solution Selection:**
   - Tracks the solution with the minimum ozone depletion.

### Simulation Details
- **Cooling Effect:** Calculated using a global cooling factor and aerosol spread rate.
- **Ozone Depletion:** Determined using an ozone depletion factor and aerosol spread rate.

---
