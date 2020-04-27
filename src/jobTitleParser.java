
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class jobTitleParser {
	
	private static ArrayList<String> parseSummaryText(String Summary) throws InterruptedException {
		ArrayList<String> parsedText = new ArrayList<String>();
		//*****************************************************************************************
		//  The two delimiters of the string are ':' and '|'. Note the pipe character needs special
		//  escape sequence as it is also the regex or operator hence the '\\|' below
		//*****************************************************************************************
		String[] rawParse = Summary.split(":|\\|");
		if (rawParse.length == 6) {
			parsedText.add(rawParse[1]);
			parsedText.add(rawParse[3]);
			parsedText.add(rawParse[5]);
		}
		else {
			parsedText.add("Failed");
		}
		return parsedText;	
	} // End parseSummaryText
	
	static String testTextWithJob = "Service: Onboarding | Service Option: NSD Deskside Hardware / Software\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"Form Fields from the original CA Service Catalog request:\r\n" + 
			"EZETP Request Information\r\n" + 
			"\r\n" + 
			"Employee Job Title Description = Sales Rep I - On/Off-Premise\r\n" + 
			"Start Date = 02/17/2020";
	
	static String testTextWithoutJob = "Service: Onboarding | Service Option: Create an Active Directory Account\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"Form Fields from the original CA Service Catalog request:\r\n" + 
			"EZETP Request Information\r\n" + 
			"\r\n" + 
			"Employee First Name = Test\r\n" + 
			"Employee Last Name = WC Rate Sales 4000\r\n" + 
			"Start Date = 02/17/2020";
	
	private static String getJobTitle(String text) throws InterruptedException {
		int titleStart = 0;
		titleStart = text.indexOf("Employee Job Title Description =");
		if (titleStart >= 0) { // Found start of a job title
			titleStart = titleStart + "Employee Job Title Description = ".length();
			text = text.substring(titleStart).split("\\r?\\n")[0];
		}
		else {
			//System.out.println("No Job Title Found");
			text = "No Job Title Found";
		}
		return text;
	}
	
	private static String getRidOfExtraNewLines(String text) throws InterruptedException {
		//return text.split("\\r?\\n");
		String[] textArray = text.split("[\\r\\n]+");
		String returnString = " ";
		for (int i = 0; i < textArray.length; i++) {
			returnString =  returnString + textArray[i] + "\r\n";
		}
		return returnString;	
	}

	private static boolean parseTitle(String[] testTitles, String title) {
		
		//***************************************************************************
		//  We want to take a job title like the following:
		//     Test One Two Three Four
		//  And generate the following strings to test
		//     Test One Two Three Four
		//          One Two Three Four
		//              Two Three Four
		//                  Three Four
		//                        Four
		//  We can then test for starts with 
		//  and also starts with followed by such as:
		//     Starts with "One" or
		//     Starts with "Two Thr"
		//       New Comment 
		//****************************************************************************
		
		String[] titleParts = title.split(" ");
		String[] titleSubstrings = titleParts;
		for (int i = 0; i < titleParts.length; i++) {
			for (int j = i+1; j < titleParts.length; j++) {
				titleSubstrings[i] = titleSubstrings[i] + " " + titleParts[j];
			}		
		}
		for (int i = 0; i < testTitles.length; i++) {
			for (int j = 0; j < titleSubstrings.length; j++) {
				if (titleSubstrings[j].startsWith(testTitles[i])) {
					//System.out.println("titlePart :" + titleParts[j] + " starts with :" + testTitles[i]);
					return true;
				}
			}
		}
		//System.out.println("Parsing Title: " + title + " Not Found");
		return false;		
	}
	
	private static boolean titleHasCommas(String title) {
		if (title.contains(",")) {
			return true;
		}
		else {
			return false;
		}
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String jobTitle = "Wine Sales Consultant, Mtns-Aspen/Snowma";
		String[] titleParts = jobTitle.split(" ");
		for (int i = 0; i < titleParts.length; i++)
		{
			System.out.println(" Part #" + i + " = " + titleParts[i]);
		}
		String test = "Dir";
		String testLower = test.toLowerCase();
		String jobTitleLower = jobTitle.toLowerCase();
		String inFileName = "C:\\Test\\position_cnt_SGWS_COM.txt";
		String outFileName = "C:\\Test\\position_cnt_SGWS_COM.csv";
		
		if (args.length > 1) {
			inFileName = args[0];
			outFileName = args[1];
		}
		try {	
			 try {
				 String testText = "Service: Onboarding | Service Option: Create an Active Directory Account | Role: Sales";
				 System.out.println("Calling parseSummaryText with:" + testText);
				 ArrayList<String> catchList = parseSummaryText(testText);
				 System.out.println("Back from parseSummaryText array size = " + catchList.size());
				 for (int i =0; i < catchList.size(); i++) {
					 System.out.println("Parse Test: " + i + " " + catchList.get(i));
				 }
				 System.out.println("Start of Parse Title Test ");
				 System.out.println("Test of text with title :" + getJobTitle(testTextWithJob));
				 System.out.println("Test of text without title :" + getJobTitle(testTextWithoutJob));
				 System.out.println("Start of new line suppression test");
				 String lines = getRidOfExtraNewLines(testTextWithJob);
				 System.out.println("New Line supression output :" + lines);
			 }
			 catch (Exception e) {
				 
			 }
			 
			List<String> lines = Files.readAllLines(Paths.get(inFileName));
		
			//System.out.println(lines.get(5));
			//***************************************************************************
			//  Initialize the test strings
			//***************************************************************************
			String[] testExec = {"VP","V.P","Pres","Vice","Dir","Chair","Chief","CEO","CFO","CIO","CMO"};
			String[] testAnal = {"Anal"};
			String[] testSales = {"Rep","Sls Cons","Sales Cons"};
			String[] testNone = {"Labor","Mer","Driver","Chauffer","Warehouse","Mechanic","Stocker","Janitor","Hostler","Retired"};
			int execCount = 0, analCount = 0, salesCount = 0, warehouseCount = 0, officeCount = 0;
			//****************************************************************************
			//  Make all the test strings lower case
			//****************************************************************************
			for (int i = 0; i < testExec.length; i++) {
				testExec[i] = testExec[i].toLowerCase();
				//System.out.println(testExec[i]);
			}
			for (int i = 0; i < testAnal.length; i++) {
				testAnal[i] = testAnal[i].toLowerCase();
			}
			for (int i = 0; i < testSales.length; i++) {
				testSales[i] = testSales[i].toLowerCase();
			}
			for (int i = 0; i < testNone.length; i++) {
				testNone[i] = testNone[i].toLowerCase();
			}
			//**************************************************************************
			//  Parse the job title to determine the role it maps to
			//**************************************************************************
			for (int i = 0; i < lines.size(); i++)
			{
				jobTitle = lines.get(i);
				jobTitleLower = jobTitle.toLowerCase();
				if (titleHasCommas(jobTitle)) {
					//******************************************************************
					// Enclose the title in  double quotes " title " so the csv file only has 3 columns
					//******************************************************************
					jobTitle = "\"".concat(jobTitle).concat("\"");
				}
				if (parseTitle(testExec, jobTitleLower)){
					//System.out.println(i + " Job Title |" + jobTitle + "| gets Executive Hardware");
					lines.set(i,"Job Title," + jobTitle + ",gets Executive Hardware");
					execCount++;
				}
				else if (parseTitle(testAnal,jobTitleLower)) {
					//System.out.println(i + " Job Title |" + jobTitle + "| gets Analist Hardware");
					lines.set(i,"Job Title," + jobTitle + ",gets Analist Hardware");
					analCount++;
				}
				else if (parseTitle(testSales,jobTitleLower)) {
					//System.out.println(i + " Job Title |" + jobTitle + "| gets Sales Hardware");
					lines.set(i,"Job Title," + jobTitle + ",gets Sales Hardware");
					salesCount++;
				}
				else if (parseTitle(testNone,jobTitleLower)) {
					//System.out.println(i + " Job Title |" + jobTitle + "| Is a position that doesn't get Hardware");
					lines.set(i,"Job Title," + jobTitle + ",gets No Hardware");
					warehouseCount++;
				}
				else {
					//System.out.println(i + " Job Title |" + jobTitle + "| gets Office Worker Hardware");
					lines.set(i,"Job Title," + jobTitle + ",gets Office Worker Hardware");
					officeCount++;
				}
			}
			//**************************************************************************************
			//  Write the results to the outputFile and print the statistics for the run
			//**************************************************************************************
			try {
				Path path = Paths.get(outFileName);
				Files.write(path, lines,StandardCharsets.UTF_8);
				System.out.println("             Output File = " + outFileName + " Created!");
				System.out.println("Executive Hardware count = " + execCount);
				System.out.println("  Analist Hardware count = " + analCount);
				System.out.println("    Sales Hardware count = " + salesCount);
				System.out.println("  Warehouse worker count = " + warehouseCount);
				System.out.println("   Office Hardware count = " + officeCount);
			} catch (IOException e) {
				System.out.println("Output File:" + outFileName + " Creation Failed!!!");
				System.out.println(e.toString());			
			}
		} catch (IOException e) {
			System.out.println("Input file open failure for: " + inFileName);
			System.out.println(e.toString());
			//e.printStackTrace();
		}
	}
}
