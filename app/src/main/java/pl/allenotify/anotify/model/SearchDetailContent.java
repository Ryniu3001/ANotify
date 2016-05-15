package pl.allenotify.anotify.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by marcin on 13.05.16.
 */
public class SearchDetailContent {

    public static final String NAME = "Name";
    public static final String TITLE = "Title";
    public static final String SEARCH_IN_DESC = "IsCheckedSearchInDescription";
    public static final String PRICE_MIN = "PriceMin";
    public static final String PRICE_MAX = "PriceMax";
    public static final String CATEGORY_ID = "CategoryId";
    public static final String CONDITION_ID = "ConditionId";
    public static final String OFFER_TYPE_ID = "OfferTypeId";
    public static final String LOCALIZATION_TYPE_ID = "SelectedTypeLocalizationId";
    public static final String STATE_ID = "StateId";
    public static final String CITY = "City";
    public static final String POST_CODE = "PostCode";
    public static final String DISTANCE_ID = "DistanceId";
    public static final String CATEGORIES = "Categories";

    public static final String SELECTED_CATEGORY_ID = "SelectedIdCategory";

    public static class SearchDetailItem{
        private String name;
        private String title;
        private Boolean searchInDesc = true;
        private Double priceMin;
        private Double priceMax;
        private Integer categoryId;
        private Integer conditionId;
        private Integer offerTypeId;
        private Integer localizationTypeId;
        private Integer stateId;
        private String city;
        private String postCode;
        private Integer distanceId;
        private List<ChosenCategory> selectedCategories;

        public String toJSON(){
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(NAME, this.name);
                jsonObject.put(TITLE, this.title);
                jsonObject.put(SEARCH_IN_DESC, this.searchInDesc);
                jsonObject.put(PRICE_MIN, this.priceMin);
                jsonObject.put(PRICE_MAX, this.priceMax);
                jsonObject.put(CATEGORY_ID, this.categoryId);
                jsonObject.put(CONDITION_ID, this.conditionId);
                jsonObject.put(OFFER_TYPE_ID, this.offerTypeId);
                jsonObject.put(LOCALIZATION_TYPE_ID, this.localizationTypeId);
                jsonObject.put(STATE_ID, this.stateId);
                jsonObject.put(CITY,this.city);
                jsonObject.put(POST_CODE, this.postCode);
                jsonObject.put(DISTANCE_ID, this.distanceId);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Boolean getSearchInDesc() {
            return searchInDesc;
        }

        public void setSearchInDesc(Boolean searchInDesc) {
            this.searchInDesc = searchInDesc;
        }

        public Double getPriceMin() {
            return priceMin;
        }

        public void setPriceMin(Double priceMin) {
            this.priceMin = priceMin;
        }

        public Double getPriceMax() {
            return priceMax;
        }

        public void setPriceMax(Double priceMax) {
            this.priceMax = priceMax;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public Integer getConditionId() {
            return conditionId;
        }

        public void setConditionId(Integer conditionId) {
            this.conditionId = conditionId;
        }

        public Integer getOfferTypeId() {
            return offerTypeId;
        }

        public void setOfferTypeId(Integer offerTypeId) {
            this.offerTypeId = offerTypeId;
        }

        public Integer getLocalizationTypeId() {
            return localizationTypeId;
        }

        public void setLocalizationTypeId(Integer localizationTypeId) {
            this.localizationTypeId = localizationTypeId;
        }

        public Integer getStateId() {
            return stateId;
        }

        public void setStateId(Integer stateId) {
            this.stateId = stateId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        public Integer getDistanceId() {
            return distanceId;
        }

        public void setDistanceId(Integer distanceId) {
            this.distanceId = distanceId;
        }

        public List<ChosenCategory> getSelectedCategories() {
            return selectedCategories;
        }

        public void setSelectedCategories(List<ChosenCategory> selectedCategories) {
            this.selectedCategories = selectedCategories;
        }
    }

    public static class ChosenCategory {
        private Integer categoryId;
        private List<CategoryContent.CategoryItem> siblings;
        private Integer categoryPositionOnSiblings; //Pole pomocnicze - pozycja wybranej kategorii na liście siblings

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public List<CategoryContent.CategoryItem> getSiblings() {
            return siblings;
        }

        public void setSiblings(List<CategoryContent.CategoryItem> siblings) {
            this.siblings = siblings;
        }

        public Integer getCategoryPositionOnSiblings() {
            return categoryPositionOnSiblings;
        }

        public void setCategoryPositionOnSiblings(Integer categoryPositionOnSiblings) {
            this.categoryPositionOnSiblings = categoryPositionOnSiblings;
        }
    }
}
