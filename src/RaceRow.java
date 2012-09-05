// Year	1st Place Rider	CountryID 1st	1st Place Rider Country	1st Place Rider Team	2nd Place Rider	c2nd	CountryID 2nd	2nd Place Rider Country	2nd Place Rider Team	3rd Place Rider	c3rd	CountryID 3rd	3rd Place Rider Country	3rd Place Rider Team	# of Stages	Distance (km)	Average Speed (km/h)	Best Team

public class RaceRow implements Comparable<RaceRow>{
	
	public int year, firstCountryID, secondCountryID, thirdCountryID,
			numStages;
	public String firstPlaceRider, firstPlaceCountry, firstPlaceTeam,
			secondPlaceRider, secondPlaceCountry, secondPlaceTeam,
			thirdPlaceRider, thirdPlaceCountry, thirdPlaceTeam, bestTeam;
	public float c2nd, c3rd, avgSpeed, distance;
	
	public RaceRow(){
		year = firstCountryID = secondCountryID = thirdCountryID =
		numStages = 0;
		firstPlaceRider = firstPlaceCountry = firstPlaceTeam =
		secondPlaceRider = secondPlaceCountry = secondPlaceTeam =
		thirdPlaceRider = thirdPlaceCountry = thirdPlaceTeam = bestTeam = null;
		c2nd = c3rd = avgSpeed = distance = 0.0f;
	}

	@Override
	public int compareTo(RaceRow o) {
		return year - o.year;
	}
	
	
}
