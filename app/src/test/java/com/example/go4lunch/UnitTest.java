package com.example.go4lunch;

import com.example.go4lunch.models.Restaurant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;

import static com.example.go4lunch.models.Restaurant.nearbyRestaurant;
import static com.example.go4lunch.models.Restaurant.nearbyRestaurantList;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UnitTest
{
    Restaurant restaurant;

    @Before
    public void addTestRestaurantToList(){
        // Creating tests objects
        HashMap<String,String> restaurantToCreate = new HashMap<>();
        restaurantToCreate.put("place_name","Hôtel & Restaurant Campanile de Cahors");
        restaurantToCreate.put("vicinity","33 Avenue de l'Europe, Cahors");
        restaurantToCreate.put("lat", "44.4677256");
        restaurantToCreate.put("lng", "1.4293046");
        restaurantToCreate.put("place_id", "ChIJF01NrbSOrBIR4Jf7X9Y-YH8");
        restaurantToCreate.put("reference", "ChIJF01NrbSOrBIR4Jf7X9Y-YH8");
        restaurantToCreate.put("rating", "3.6");
        restaurantToCreate.put("open_now", "true");
        restaurantToCreate.put("photo_reference", "CmRaAAAA4jdXa_e9KFVeq1D-oR96PHZ-n00PJjyWYoZbiJjQayPDkuljjg_c87SojfqloFLlA_L8ZIvfjiDjaS0LeqKFtZEHWTLrpfq1EsC4PoYhoylXIAxhyXCsKrFjTo17-6IgEhAfaOOfvMBs-Z73ShcFjCpDGhRM2ZnonPCAyTQzsurS5uSTkg6DrQ");
        restaurantToCreate.put("photo_width", "1200");
        restaurantToCreate.put("phone_number", "05 65 22 20 21");
        restaurantToCreate.put("website", "https://cahors.campanile.com/");
        restaurantToCreate.put("photo_url", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=CmRaAAAA4jdXa_e9KFVeq1D-oR96PHZ-n00PJjyWYoZbiJjQayPDkuljjg_c87SojfqloFLlA_L8ZIvfjiDjaS0LeqKFtZEHWTLrpfq1EsC4PoYhoylXIAxhyXCsKrFjTo17-6IgEhAfaOOfvMBs-Z73ShcFjCpDGhRM2ZnonPCAyTQzsurS5uSTkg6DrQ&key=AIzaSyCt0sAopDinlWW_70N9CkVlo1TOahLHEhk");

        restaurant = new Restaurant(restaurantToCreate, null);
        nearbyRestaurantList.add(restaurantToCreate);
        nearbyRestaurant.add(restaurant);

        restaurantToCreate.put("place_name","Château de Mercuès");
        restaurantToCreate.put("vicinity","Rue du Château, Mercuès");
        restaurantToCreate.put("lat", "44.48955400000001");
        restaurantToCreate.put("lng", "1.354167");
        restaurantToCreate.put("place_id", "ChIJjQolgQ-PrBIRQzLzp5F3I4c");
        restaurantToCreate.put("reference", "ChIJjQolgQ-PrBIRQzLzp5F3I4c");
        restaurantToCreate.put("rating", "4.4");
        restaurantToCreate.put("open_now", "true");
        restaurantToCreate.put("photo_reference", "CmRaAAAAbWB7m7mnYnaThUVqM98n28iHvxAaDo1GROycK0rgapMnksyAf0oarB4I9bZD-UfRERNaRO6AhZQtVkyjvnjXg6zxOzB0AZMfA02NbAYaK-DhExpnbHNPloYIuKZzm0RgEhA6f8jp89fqrxcX3P4u7ARvGhRLTzKS7pb_XidxDv_PPe0Z3uzwmg");
        restaurantToCreate.put("photo_width", "1333");
        restaurantToCreate.put("phone_number", "05 65 20 00 01");
        restaurantToCreate.put("website", "http://chateaudemercues.com/");
        restaurantToCreate.put("photo_url", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1333&photoreference=CmRaAAAAbWB7m7mnYnaThUVqM98n28iHvxAaDo1GROycK0rgapMnksyAf0oarB4I9bZD-UfRERNaRO6AhZQtVkyjvnjXg6zxOzB0AZMfA02NbAYaK-DhExpnbHNPloYIuKZzm0RgEhA6f8jp89fqrxcX3P4u7ARvGhRLTzKS7pb_XidxDv_PPe0Z3uzwmg");

        restaurant = new Restaurant(restaurantToCreate, null);
        nearbyRestaurantList.add(restaurantToCreate);
        nearbyRestaurant.add(restaurant);

        restaurantToCreate.put("place_name","HOTEL RESTAURANT LE VINOIS");
        restaurantToCreate.put("vicinity","Mas de Lacroix, Caillac");
        restaurantToCreate.put("lat", "44.4674671");
        restaurantToCreate.put("lng", "1.4184169");
        restaurantToCreate.put("place_id", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("reference", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("rating", "4.1");
        restaurantToCreate.put("open_now", "false");
        restaurantToCreate.put("photo_reference", "CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");
        restaurantToCreate.put("photo_width", "3072");
        restaurantToCreate.put("phone_number", "05 65 30 53 60");
        restaurantToCreate.put("website", "http://www.levinois.com/");
        restaurantToCreate.put("photo_url", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=3072&photoreference=CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");

        restaurant = new Restaurant(restaurantToCreate, null);
        nearbyRestaurantList.add(restaurantToCreate);
        nearbyRestaurant.add(restaurant);
    }

    @Test
    public void shouldRetrieveRestaurantAsHashMap(){
        HashMap<String,String> restaurantToCreate = new HashMap<>();
        restaurantToCreate.put("place_name","HOTEL RESTAURANT LE VINOIS");
        restaurantToCreate.put("vicinity","Mas de Lacroix, Caillac");
        restaurantToCreate.put("lat", "44.4674671");
        restaurantToCreate.put("lng", "1.4184169");
        restaurantToCreate.put("place_id", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("reference", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("rating", "4.1");
        restaurantToCreate.put("open_now", "false");
        restaurantToCreate.put("photo_reference", "CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");
        restaurantToCreate.put("photo_width", "3072");
        restaurantToCreate.put("phone_number", "05 65 30 53 60");
        restaurantToCreate.put("website", "http://www.levinois.com/");
        restaurantToCreate.put("photo_url", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=3072&photoreference=CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");
        restaurant = new Restaurant(restaurantToCreate, null);

        assertEquals(restaurant.getInstanceAsHashMap().get("place_name"), restaurantToCreate.get("place_name"));
        assertEquals(restaurant.getInstanceAsHashMap().get("vicinity"), restaurantToCreate.get("vicinity"));
        assertEquals(restaurant.getInstanceAsHashMap().get("lat"), restaurantToCreate.get("lat"));
        assertEquals(restaurant.getInstanceAsHashMap().get("lng"), restaurantToCreate.get("lng"));
        assertEquals(restaurant.getInstanceAsHashMap().get("place_id"), restaurantToCreate.get("place_id"));
        assertEquals(restaurant.getInstanceAsHashMap().get("reference"), restaurantToCreate.get("reference"));
        assertEquals(restaurant.getInstanceAsHashMap().get("rating"), restaurantToCreate.get("rating"));
        assertEquals(restaurant.getInstanceAsHashMap().get("open_now"), restaurantToCreate.get("open_now"));
        assertEquals(restaurant.getInstanceAsHashMap().get("photo_width"), restaurantToCreate.get("photo_width"));
        assertEquals(restaurant.getInstanceAsHashMap().get("phone_number"), restaurantToCreate.get("phone_number"));
        assertEquals(restaurant.getInstanceAsHashMap().get("website"), restaurantToCreate.get("website"));
        assertEquals(restaurant.getInstanceAsHashMap().get("photo_url"), restaurantToCreate.get("photo_url"));
    }

    @Test
    public void shouldRetrieveRestaurantById(){
        HashMap<String,String> restaurantToCreate = new HashMap<>();
        restaurantToCreate.put("place_name","HOTEL RESTAURANT LE VINOIS");
        restaurantToCreate.put("vicinity","Mas de Lacroix, Caillac");
        restaurantToCreate.put("lat", "44.4674671");
        restaurantToCreate.put("lng", "1.4184169");
        restaurantToCreate.put("place_id", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("reference", "ChIJS44pzZOFrBIRPDQmjBSv-jI");
        restaurantToCreate.put("rating", "4.1");
        restaurantToCreate.put("open_now", "false");
        restaurantToCreate.put("photo_reference", "CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");
        restaurantToCreate.put("photo_width", "3072");
        restaurantToCreate.put("phone_number", "05 65 30 53 60");
        restaurantToCreate.put("website", "http://www.levinois.com/");
        restaurantToCreate.put("photo_url", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=3072&photoreference=CmRaAAAAaHP4G6WXrMrmkcPzLt20K2E8WPO1R6LA7_9JtUsedcL-FNAmIkgmXIov-RfwblPH0FsrKupHoAVMwGodWCZUERJ1lmUMtQZlPTJtmtNw8MU_qpL8_uWszQfbjr1m_6iFEhCAyqDyqq-nh0nm13GlODj8GhTtwcmeLO6F7EvaLMLwLKp0-b5W3Q");
        restaurant = new Restaurant(restaurantToCreate, null);

        Restaurant retrievedRestaurant = Restaurant.searchByPlaceId("ChIJS44pzZOFrBIRPDQmjBSv-jI");

        assertEquals(restaurant.getInstanceAsHashMap().get("place_name"), retrievedRestaurant.getInstanceAsHashMap().get("place_name"));
        assertEquals(restaurant.getInstanceAsHashMap().get("vicinity"), retrievedRestaurant.getInstanceAsHashMap().get("vicinity"));
        assertEquals(restaurant.getInstanceAsHashMap().get("lat"), retrievedRestaurant.getInstanceAsHashMap().get("lat"));
        assertEquals(restaurant.getInstanceAsHashMap().get("lng"), retrievedRestaurant.getInstanceAsHashMap().get("lng"));
        assertEquals(restaurant.getInstanceAsHashMap().get("place_id"), retrievedRestaurant.getInstanceAsHashMap().get("place_id"));
        assertEquals(restaurant.getInstanceAsHashMap().get("reference"), retrievedRestaurant.getInstanceAsHashMap().get("reference"));
        assertEquals(restaurant.getInstanceAsHashMap().get("rating"), retrievedRestaurant.getInstanceAsHashMap().get("rating"));
        assertEquals(restaurant.getInstanceAsHashMap().get("open_now"), retrievedRestaurant.getInstanceAsHashMap().get("open_now"));
        assertEquals(restaurant.getInstanceAsHashMap().get("photo_width"), retrievedRestaurant.getInstanceAsHashMap().get("photo_width"));
        assertEquals(restaurant.getInstanceAsHashMap().get("phone_number"), retrievedRestaurant.getInstanceAsHashMap().get("phone_number"));
        assertEquals(restaurant.getInstanceAsHashMap().get("website"), retrievedRestaurant.getInstanceAsHashMap().get("website"));
        assertEquals(restaurant.getInstanceAsHashMap().get("photo_url"), retrievedRestaurant.getInstanceAsHashMap().get("photo_url"));
    }
}