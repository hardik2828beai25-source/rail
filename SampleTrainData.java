package data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import model.Train;
import model.TravelClass;

/**
 * SampleTrainData.java
 * ---------------------------------------------------------------
 * The FIXED sample data of the project: the 13 stations and the
 * FICTIONAL trains. The numbers, names, timings and fares are made up;
 * they are NOT real IRCTC data.
 *
 * Later this class can be replaced by code that reads a database.
 */
public final class SampleTrainData {

    /** The 13 stations of this project, sorted A-Z. Only these stations can be used. */
    public static final List<String> STATIONS = List.of(
            "Amritsar", "Bhopal", "Chandigarh", "Chennai", "Delhi", "Jaipur", "Jammu",
            "Kanpur", "Katra", "Kolkata", "Ludhiana", "Mumbai", "Varanasi");

    private SampleTrainData() {
        // utility class: no objects needed
    }

    /**
     * Creates the list of sample trains.
     * Each train runs directly from ONE source to ONE destination.
     * Format: number, name, source, destination, departure (24h),
     *         journey minutes, base fare, travel classes.
     */
    public static List<Train> createTrains() {
        List<Train> trains = new ArrayList<>();

        EnumSet<TravelClass> shatabdi  = EnumSet.of(TravelClass.AC_CHAIR_CAR, TravelClass.EXECUTIVE_CLASS);
        EnumSet<TravelClass> rajdhani  = EnumSet.of(TravelClass.FIRST_AC, TravelClass.SECOND_AC, TravelClass.THIRD_AC);
        EnumSet<TravelClass> garibRath = EnumSet.of(TravelClass.THIRD_AC, TravelClass.AC_3_ECONOMY);
        EnumSet<TravelClass> express   = EnumSet.of(TravelClass.FIRST_AC, TravelClass.SECOND_AC, TravelClass.THIRD_AC,
                TravelClass.SLEEPER, TravelClass.SECOND_SITTING, TravelClass.GENERAL);
        EnumSet<TravelClass> superfast = EnumSet.of(TravelClass.FIRST_AC, TravelClass.SECOND_AC, TravelClass.THIRD_AC,
                TravelClass.AC_3_ECONOMY, TravelClass.SLEEPER, TravelClass.GENERAL);
        EnumSet<TravelClass> intercity = EnumSet.of(TravelClass.AC_CHAIR_CAR, TravelClass.SECOND_SITTING, TravelClass.GENERAL);

        // ---- Amritsar <-> Delhi (popular: 3 trains each way) ----
        addTrain(trains, "91011", "Amritsar Shatabdi",      "Amritsar", "Delhi",      "06:00",  330, 400, shatabdi);
        addTrain(trains, "91013", "Golden Temple Express",  "Amritsar", "Delhi",      "14:30",  480, 380, express);
        addTrain(trains, "91015", "Punjab Mail Superfast",  "Amritsar", "Delhi",      "21:15",  465, 420, superfast);
        addTrain(trains, "91012", "Delhi Shatabdi",         "Delhi",    "Amritsar",   "17:15",  335, 400, shatabdi);
        addTrain(trains, "91014", "Amritsar Express",       "Delhi",    "Amritsar",   "09:00",  495, 380, express);
        addTrain(trains, "91016", "Punjab Mail Superfast",  "Delhi",    "Amritsar",   "22:30",  450, 420, superfast);

        // ---- Delhi <-> Mumbai ----
        addTrain(trains, "92301", "Mumbai Rajdhani",        "Delhi",    "Mumbai",     "16:30",  975, 900, rajdhani);
        addTrain(trains, "92303", "Mumbai Garib Rath",      "Delhi",    "Mumbai",     "19:00", 1140, 600, garibRath);
        addTrain(trains, "92302", "Delhi Rajdhani",         "Mumbai",   "Delhi",      "17:00",  990, 900, rajdhani);

        // ---- Delhi <-> Kolkata ----
        addTrain(trains, "91201", "Kolkata Rajdhani",       "Delhi",    "Kolkata",    "16:55", 1020, 950, rajdhani);
        addTrain(trains, "91203", "Howrah Superfast",       "Delhi",    "Kolkata",    "20:10", 1200, 800, superfast);
        addTrain(trains, "91202", "Delhi Rajdhani Kolkata", "Kolkata",  "Delhi",      "16:50", 1005, 950, rajdhani);

        // ---- Delhi <-> Chandigarh ----
        addTrain(trains, "92201", "Chandigarh Shatabdi",    "Delhi",    "Chandigarh", "07:40",  195, 350, shatabdi);
        addTrain(trains, "92203", "Himalayan Intercity",    "Delhi",    "Chandigarh", "15:20",  270, 220, intercity);

        // ---- Delhi <-> Jaipur ----
        addTrain(trains, "91401", "Jaipur Intercity",       "Jaipur",   "Delhi",      "06:00",  270, 250, intercity);
        addTrain(trains, "91403", "Pink City Express",      "Jaipur",   "Delhi",      "16:45",  330, 270, express);
        addTrain(trains, "92401", "Jaipur Shatabdi",        "Delhi",    "Jaipur",     "06:10",  270, 300, shatabdi);

        // ---- Delhi <-> Jammu / Katra ----
        addTrain(trains, "91501", "Jammu Rajdhani",         "Jammu",    "Delhi",      "19:20",  600, 700, rajdhani);
        addTrain(trains, "91503", "Tawi Express",           "Jammu",    "Delhi",      "21:45",  720, 450, express);
        addTrain(trains, "92501", "Delhi Tawi Express",     "Delhi",    "Jammu",      "20:30",  660, 450, express);
        addTrain(trains, "91601", "Vaishno Devi Link",      "Katra",    "Amritsar",   "07:30",  300, 220, intercity);
        addTrain(trains, "91602", "Katra Link Express",     "Amritsar", "Katra",      "14:00",  300, 220, intercity);

        // ---- Punjab / Chandigarh ----
        addTrain(trains, "91701", "Ludhiana Intercity",     "Ludhiana", "Chandigarh", "08:15",  120, 120, intercity);
        addTrain(trains, "91101", "Mumbai Superfast",       "Chandigarh","Mumbai",    "11:00", 1620, 1000, superfast);

        // ---- Varanasi, Bhopal, Kanpur, Chennai ----
        addTrain(trains, "91801", "Kashi Superfast",        "Varanasi", "Delhi",      "17:30",  720, 700, superfast);
        addTrain(trains, "91802", "Delhi Kashi Superfast",  "Delhi",    "Varanasi",   "19:30",  780, 700, superfast);
        addTrain(trains, "91901", "Bhopal Mumbai Express",  "Bhopal",   "Mumbai",     "20:15",  780, 550, superfast);
        addTrain(trains, "92001", "Kanpur Howrah Express",  "Kanpur",   "Kolkata",    "18:00",  960, 550, express);
        addTrain(trains, "92101", "Chennai Howrah Mail",    "Chennai",  "Kolkata",    "14:10", 1650, 900, superfast);

        // ---- Mumbai <-> Chennai ----
        addTrain(trains, "91301", "Chennai Superfast",      "Mumbai",   "Chennai",    "11:40", 1260, 800, superfast);
        addTrain(trains, "91303", "Chennai Express",        "Mumbai",   "Chennai",    "21:00", 1380, 700, express);

        return trains;
    }

    private static void addTrain(List<Train> trains, String number, String name, String source,
                                 String destination, String departure, int durationMinutes,
                                 int baseFare, EnumSet<TravelClass> classes) {
        trains.add(new Train(number, name, source, destination, departure,
                durationMinutes, baseFare, classes));
    }
}
