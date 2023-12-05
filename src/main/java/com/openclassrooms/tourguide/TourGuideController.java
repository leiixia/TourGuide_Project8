package com.openclassrooms.tourguide;


import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.tourguide.DTO.AttractionDTO;
import com.openclassrooms.tourguide.service.RewardsService;
import gpsUtil.location.Attraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

    @Autowired
    RewardsService rewardsService;

	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws JsonProcessingException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        List<AttractionDTO> dtos = toAttractionDTOList(tourGuideService.getNearByAttractions(visitedLocation), visitedLocation);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(dtos);
    }

    private List<AttractionDTO> toAttractionDTOList (List<Attraction> attractions, VisitedLocation visitedLocation) {
        List<AttractionDTO> result = new ArrayList<>();
        for(Attraction a : attractions) {
            AttractionDTO attractionDTO = new AttractionDTO();
            attractionDTO.setAttractionName(a.attractionName);
            attractionDTO.setAttractionLatitude(a.latitude);
            attractionDTO.setAttractionLongitude(a.longitude);
            attractionDTO.setAttractionLongitude(visitedLocation.location.longitude);
            attractionDTO.setAttractionLatitude(visitedLocation.location.latitude);
            attractionDTO.setUserLatitude(attractionDTO.getUserLatitude());
            attractionDTO.setUserLongitude(attractionDTO.getUserLongitude());
            attractionDTO.setMilesDistance(rewardsService.getDistance(visitedLocation.location, a));
            attractionDTO.setRewardPoints(attractionDTO.rewardPoints);
            result.add(attractionDTO);
        }
            return result;
    }

    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}