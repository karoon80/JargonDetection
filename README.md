JargonDetection
===============

Play the app to detect a jargon and give what it represents
* The home page of the app includes a list of visited locations with a title that demonstrates the jargon representation and the first line of the postal address.
* The user can click any location on the list and get information about the visited place such as postal address and what that location represents; for example, is it a restaurant, is it an entertainment place, etc...
* From the home page, the user can start speech recognition API to convert speech to text.
* After the scan finishes, a new page appears to display the location on Google Map, the postal address, and the jargon translation by checking each word in the sentence with all jargons  available in the database. This page is titled by "Current Location Information".
* From this page, the user can go back to Home page by clicking on the home icon on the top right corner of the action bar. As a result, the user can see the current location added into the list.
* The available jargons for this version are: 
* String [] bio = {"chromatid","chromosome","genetic","gene","cytogenetic","embryo","epistatic"
					,"anomaly","cell","germ","mutation","pathogenic","unigenic"};
* String [] computer = {"android","antivirus","ios","app","auto complete","bandwidth",
					"bit","cable","sensor","console","cpu","processor","database","bluetooth","bug","mobile","gage"};
* String [] home = {"home","house","place","room","kitchen","bathroom"};
* String [] gym = {"excercise","workout","treadmill","zumba","elliptical","cycling","turbo","workingout","exercising"};
* String [] food = {"restaurant","lunch","breakfast","dinner","fries","egg","steak","chicken","bacon"
					,"pizza","kabob","rice"};
* String [] entertainment = {"fun","fountain","santa","theater","trolley"};
