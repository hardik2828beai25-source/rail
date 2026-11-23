package fare;

import model.Quota;
import model.Train;
import model.TravelClass;

public class FareCalculator {


    public int getFarePerPassenger(Train train, TravelClass travelClass, Quota quota) {
        return train.getFare(travelClass, quota);
    }

    
    public int calculateTotalFare(Train train, TravelClass travelClass, Quota quota, int passengerCount) {
        return getFarePerPassenger(train, travelClass, quota) * passengerCount;
    }
}
