package train;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.Train;
public class TrainSearchService {

    private final List<Train> trains;

    public TrainSearchService(List<Train> trains) {
        this.trains = new ArrayList<>(trains);
    }

    public List<Train> searchTrains(String source, String destination) {
        List<Train> result = new ArrayList<>();
        for (Train t : trains) {
            if (t.runsBetween(source, destination)) {
                result.add(t);
            }
        }
        return result;
    }
    public List<String> getDestinationsFrom(String source) {
        Set<String> destinations = new TreeSet<>();
        for (Train t : trains) {
            if (t.getSource().equalsIgnoreCase(source)) {
                destinations.add(t.getDestination());
            }
        }
        return new ArrayList<>(destinations);
    }
}
// files 