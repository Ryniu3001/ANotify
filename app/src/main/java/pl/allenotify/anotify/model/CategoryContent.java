package pl.allenotify.anotify.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcin on 07.05.16.
 * Zawiera identyfikatory pól oraz listę kategorii otrzymanych z usługi.
 * Klasa zagnieżdżona reprezentuje obiekt kategorii.
 */
public class CategoryContent {

    public List<CategoryItem> items = new ArrayList<CategoryItem>();

    public static final String ID = "Id";
    public static final String NAME = "Name";

    public List<CategoryItem> getItems() {
        return items;
    }

    public void setItems(List<CategoryItem> items) {
        this.items = items;
    }

    public static class CategoryItem {
        private final String id;
        private final String name;

        public CategoryItem(final String id, final String name){
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
