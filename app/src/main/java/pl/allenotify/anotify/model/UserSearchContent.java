package pl.allenotify.anotify.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Zawiera identyfikatory pól usługi oraz listę wyszukiwanych przedmiotów.
 * Klasa zagnieżdżona reprezentuje obiekt wyszukiwania
 */
public class UserSearchContent {

    public static List<UserSearchItem> items = new ArrayList<UserSearchItem>();

    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String DATE = "Date";
    public static final String COUNT_FOUND = "CountFound";
    public static final String STATUS = "Status";

    public static class UserSearchItem {
        private String id;
        private String name;
        private String countFound;
        private String date;
        private String status;

        public UserSearchItem(String id, String name, String countFound, String date, String status) {
            this.id = id;
            this.name = name;
            this.countFound = countFound;
            this.date = date;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountFound() {
            return countFound;
        }

        public void setCountFound(String countFound) {
            this.countFound = countFound;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatusId(String statusId) {
            this.status = statusId;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
