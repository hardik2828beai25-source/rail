package train;

import java.util.ArrayList;
import java.util.List;

import data.SampleTrainData;

public class StationService {

    public List<String> getStations() {
        return new ArrayList<>(SampleTrainData.STATIONS);
    }

   
    public String findStation(String input) {
        String cleaned = input.trim().replaceAll("\\s+", " ");
        for (String station : SampleTrainData.STATIONS) {
            if (station.equalsIgnoreCase(cleaned)) {
                return station;
            }
        }
        return null;
    }
}
