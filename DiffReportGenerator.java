/**
 * 
 */
package com.esspl.differential_report;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @author Shubham
 *
 */
public class DiffReportGenerator {
	static String currMonth="";
    static String prevMonth="";
    static HashMap<Integer,String> hm=new HashMap<Integer,String>(); 
    static String filePath=System.getProperty("user.dir");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(":filePath:"+filePath);
		String graffiti="\r\n" + 
				"#   +-+-+-+-+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+\r\n" + 
				"#   |D|i|f|f|e|r|e|n|t|i|a|l| |R|e|p|o|r|t|\r\n" + 
				"#   +-+-+-+-+-+-+-+-+-+-+-+-+ +-+-+-+-+-+-+\r\n" + 
				"";
		System.out.println("\n "+graffiti);
	    Statement statement;
		try {
			hm.put(1,"JAN");  
	        hm.put(2,"FEB");
	        hm.put(3,"MAR");
	        hm.put(4,"APR");
	        hm.put(5,"MAY");
	        hm.put(6,"JUN");
	        hm.put(7,"JUL");
	        hm.put(8,"AUG");
	        hm.put(9,"SEP");
	        hm.put(10,"OCT");
	        hm.put(11,"NOV");
	        hm.put(12,"DEC");
	        
	        Scanner in = new Scanner(System.in);
	        System.out.println("Please enter year. Ex. 2019");
	        int year = Integer.parseInt(in.nextLine());
	        //System.out.println("Input Year is: "+year);
	        
	        System.out.println("\nPlease enter Month");
	        int month = Integer.parseInt(in.nextLine());
	        //System.out.println("Input Month is: "+month);
	        
	        currMonth = hm.get(month); 
	        prevMonth = hm.get(month-1); 
	        
	        System.out.println("\nPlease enter Report Type(1,2,3):\n 1.Hierarchy Parent.\n 2.Hierarchy PT Code \n 3.Stockist Terr Map  ");
	        int type = Integer.parseInt(in.nextLine());
	        
	        System.out.println("Input Type is: "+type);
	        String noOfUser=readConfig("Users");
	    	int userCount =Integer.parseInt(noOfUser);
	    	String details =readConfig("details");
	    	System.out.println("After Reading From Config File:"+details);
	    	 switch(type)
	         {
	         case 1:
	         	System.out.println("Preparing Report for Hierarachy Parent");
	         	processData(year,month,(month-1),currMonth,prevMonth,details,"Hierarchy Parent");
	         	break;
	         case 2:
	        	 System.out.println("Preparing Report for Hierarchy-PT Code");
		         processDataPT(year,month,(month-1),currMonth,prevMonth,details,"Hierarchy PT Code");
		         break;
	         case 3:
	        	 System.out.println("Preparing Report for Stockist Terr Map");
		         processDataSTM(year,month,(month-1),currMonth,prevMonth,details,"Stockist Terr Map");
		         break;
	         	default:
	         	System.out.println("You've choosen wrong parameter");
	         	break;
	         }
		}
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	     
	}
	/***
	 * 
	 * @param year
	 * @param month
	 * @param prevmonth
	 * @param currMonth2
	 * @param prevMonth2
	 * @param details
	 * @param fileName
	 */
	private static void processDataSTM(int year, int month, int prevmonth, String currMonth2, String prevMonth2, String details,
			String fileName) {
		// TODO Auto-generated method stub
		System.out.println("Recevied Parameters are year:"+year+":month:"+month+":prevmonth:"+prevmonth+":CurrMonth2:"+currMonth2+":prevMonth2:"+":details:"+details);
		String[] userWise =details.split("\\$");
		System.out.println("userWise:"+userWise[0]);
    	System.out.println("userWise Lenght:"+userWise.length);
    	String fileOwner ="";
    	String divisions="";
    	for(int i=0;i<userWise.length;i++)
    	{
    		String[] div =userWise[i].split(":");
    		System.out.println("Inner Details"+div[0]);
    		fileOwner=div[0];
    		divisions=div[1];
    		System.out.println("Inner Details length"+div.length);
    		try
    		{
    			
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    		Connection conn = DriverManager.getConnection("jdbc:sqlserver://10.149.161.133:21443;user=sqluser;password=p@ssword@123;database=SALESPORTAL");
            System.out.println("test");
    		Statement statement = conn.createStatement();
    		String [] divs = divisions.split("#");
    		System.out.println("divs length"+divs.length);
    		XSSFWorkbook workbook = new XSSFWorkbook(); 
			XSSFSheet  spreadsheet = null;
    		for (int k=0;k<divs.length;k++)
            {
    			ResultSet resultSet = statement.executeQuery(createQuery(year,month,prevmonth,currMonth2,prevMonth2,divs[k],3));
    			System.out.println("Current k value"+k+"also value"+divs[k]);
    			spreadsheet = workbook.createSheet(fileName+"-"+divs[k]);
    			XSSFRow row = spreadsheet.createRow(0);
    			XSSFCell cell;
    			CellStyle style = workbook.createCellStyle();
    			style.setFillForegroundColor(IndexedColors.GREEN.getIndex());  
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
    			cell = row.createCell(0);
    			cell.setCellValue(currMonth2+"_EHIER_CD");
    			cell.setCellStyle(style);
    			cell = row.createCell(1);
    			cell.setCellValue(currMonth2+"_STOCKIST_CODE");
    			cell.setCellStyle(style);
    			cell = row.createCell(2);
    			cell.setCellStyle(style);
    			cell.setCellValue(currMonth2+"_MONTH");
    			cell = row.createCell(3);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_EHIER_CD");
    			cell = row.createCell(4);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_STOCKIST_CODE");
    			cell = row.createCell(5);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_MONTH");
    			int j = 1;
    			while(resultSet.next()) {
    			     row = spreadsheet.createRow(j);
    			     cell = row.createCell(0);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_EHIER_CD"));
    			     cell = row.createCell(1);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_STOCKIST_CODE"));
    			     cell = row.createCell(2);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_MONTH"));
    			     cell = row.createCell(3);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_EHIER_CD"));
    			     cell = row.createCell(4);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_STOCKIST_CODE"));
    			     cell = row.createCell(5);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_MONTH"));
    			     j++;
    		}
    			spreadsheet.autoSizeColumn(0);
        		spreadsheet.autoSizeColumn(1);
        		spreadsheet.autoSizeColumn(2);
        		spreadsheet.autoSizeColumn(3);
        		spreadsheet.autoSizeColumn(4);
        		spreadsheet.autoSizeColumn(5);
            }
    		POIXMLProperties xmlProps = workbook.getProperties();    
			POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
			coreProps.setCreator("Shubham Shah");	
			coreProps.setDescription("ESS - Differential Report ");	 
    		FileOutputStream out = null;
    			 new File(filePath+"\\"+fileName).mkdirs();
    			 out = new FileOutputStream(new File(filePath+"\\"+fileName+"\\"+fileName+"-"+fileOwner+".xlsx"));
    			 workbook.write(out);
    				out.close();
    			    System.out.println("File written successfully");
    		} catch (SQLException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}

	/***
	 * 
	 * @param year
	 * @param month
	 * @param prevmonth
	 * @param currMonth2
	 * @param prevMonth2
	 * @param details
	 * @param fileName
	 */
	private static void processDataPT(int year, int month, int prevmonth, String currMonth2, String prevMonth2, String details,
			String fileName) {
		// TODO Auto-generated method stub
		System.out.println("Recevied Parameters are year:"+year+":month:"+month+":prevmonth:"+prevmonth+":CurrMonth2:"+currMonth2+":prevMonth2:"+":details:"+details);
		String[] userWise =details.split("\\$");
		System.out.println("userWise:"+userWise[0]);
    	System.out.println("userWise Lenght:"+userWise.length);
    	String fileOwner ="";
    	String divisions="";
    	for(int i=0;i<userWise.length;i++)
    	{
    		String[] div =userWise[i].split(":");
    		System.out.println("Inner Details"+div[0]);
    		fileOwner=div[0];
    		divisions=div[1];
    		System.out.println("Inner Details length"+div.length);
    		try
    		{
    			
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    		Connection conn = DriverManager.getConnection("jdbc:sqlserver://10.149.161.133:21443;user=sqluser;password=p@ssword@123;database=SALESPORTAL");
            System.out.println("test");
    		Statement statement = conn.createStatement();
    		String [] divs = divisions.split("#");
    		System.out.println("divs length"+divs.length);
    		XSSFWorkbook workbook = new XSSFWorkbook(); 
			XSSFSheet  spreadsheet = null;
    		for (int k=0;k<divs.length;k++)
            {
    			ResultSet resultSet = statement.executeQuery(createQuery(year,month,prevmonth,currMonth2,prevMonth2,divs[k],2));
    			System.out.println("Current k value"+k+"also value"+divs[k]);
    			spreadsheet = workbook.createSheet(fileName+"-"+divs[k]);
    			XSSFRow row = spreadsheet.createRow(0);
    			XSSFCell cell;
    			CellStyle style = workbook.createCellStyle();
    			style.setFillForegroundColor(IndexedColors.GREEN.getIndex());  
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
    			cell = row.createCell(0);
    			cell.setCellValue(currMonth2+"_EHIER_CD");
    			cell.setCellStyle(style);
    			cell = row.createCell(1);
    			cell.setCellValue(currMonth2+"_POOL_CODE");
    			cell.setCellStyle(style);
    			cell = row.createCell(2);
    			cell.setCellStyle(style);
    			cell.setCellValue(currMonth2+"_MONTH");
    			cell = row.createCell(3);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_EHIER_CD");
    			cell = row.createCell(4);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_POOL_CODE");
    			cell = row.createCell(5);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_MONTH");
    			int j = 1;
    			while(resultSet.next()) {
    			     row = spreadsheet.createRow(j);
    			     cell = row.createCell(0);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_EHIER_CD"));
    			     cell = row.createCell(1);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_POOL_CODE"));
    			     cell = row.createCell(2);
    			     cell.setCellValue(resultSet.getString(currMonth2+"__MONTH"));
    			     cell = row.createCell(3);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_EHIER_CD"));
    			     cell = row.createCell(4);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_POOL_CODE"));
    			     cell = row.createCell(5);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_MONTH"));
    			     j++;
    		}
    			spreadsheet.autoSizeColumn(0);
        		spreadsheet.autoSizeColumn(1);
        		spreadsheet.autoSizeColumn(2);
        		spreadsheet.autoSizeColumn(3);
        		spreadsheet.autoSizeColumn(4);
        		spreadsheet.autoSizeColumn(5);
            }
    		POIXMLProperties xmlProps = workbook.getProperties();    
			POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
			coreProps.setCreator("Shubham Shah");	
			coreProps.setDescription("ESS - Differential Report ");
    			 FileOutputStream out = null;
    			 new File(filePath+"\\"+fileName).mkdirs();
    			 out = new FileOutputStream(new File(filePath+"\\"+fileName+"\\"+fileName+"-"+fileOwner+".xlsx"));
    			 workbook.write(out);
    				out.close();
    			    System.out.println("File written successfully");
    		} catch (SQLException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	/***
	 * 
	 * @param year
	 * @param month
	 * @param prevmonth
	 * @param currMonth2
	 * @param prevMonth2
	 * @param details
	 */
	private static void processData(int year, int month, int prevmonth, String currMonth2, String prevMonth2, String details,String fileName) {
		// TODO Auto-generated method stub
		System.out.println("Recevied Parameters are year:"+year+":month:"+month+":prevmonth:"+prevmonth+":CurrMonth2:"+currMonth2+":prevMonth2:"+":details:"+details);
		String[] userWise =details.split("\\$");
		System.out.println("userWise:"+userWise[0]);
    	System.out.println("userWise Lenght:"+userWise.length);
    	String fileOwner ="";
    	String divisions="";
    	for(int i=0;i<userWise.length;i++)
    	{
    		String[] div =userWise[i].split(":");
    		System.out.println("Inner Details"+div[0]);
    		fileOwner=div[0];
    		divisions=div[1];
    		System.out.println("Inner Details length"+div.length);
    		try
    		{
    			
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    		Connection conn = DriverManager.getConnection("jdbc:sqlserver://10.149.161.133:21443;user=sqluser;password=p@ssword@123;database=SALESPORTAL");
            System.out.println("test");
    		Statement statement = conn.createStatement();
    		String [] divs = divisions.split("#");
    		System.out.println("divs length"+divs.length);
    		XSSFWorkbook workbook = new XSSFWorkbook(); 
			XSSFSheet  spreadsheet = null;
    		for (int k=0;k<divs.length;k++)
            {
    			ResultSet resultSet = statement.executeQuery(createQuery(year,month,prevmonth,currMonth2,prevMonth2,divs[k],1));
    			System.out.println("Current k value"+k+"also value"+divs[k]);
    			spreadsheet = workbook.createSheet(fileName+"-"+divs[k]);
    			XSSFRow row = spreadsheet.createRow(0);
    			XSSFCell cell;
    			CellStyle style = workbook.createCellStyle();
    			style.setFillForegroundColor(IndexedColors.GREEN.getIndex());  
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setBorderTop(BorderStyle.MEDIUM);
                style.setBorderBottom(BorderStyle.MEDIUM);
                style.setBorderLeft(BorderStyle.MEDIUM);
                style.setBorderRight(BorderStyle.MEDIUM);
    			cell = row.createCell(0);
    			cell.setCellValue(currMonth2+"_EHIER_CD");
    			cell.setCellStyle(style);
    			cell = row.createCell(1);
    			cell.setCellValue(currMonth2+"_EHIER_PARCD");
    			cell.setCellStyle(style);
    			cell = row.createCell(2);
    			cell.setCellStyle(style);
    			cell.setCellValue(currMonth2+"_MONTH");
    			cell = row.createCell(3);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_EHIER_CD");
    			cell = row.createCell(4);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_EHIER_PARCD");
    			cell = row.createCell(5);
    			cell.setCellStyle(style);
    			cell.setCellValue(prevMonth2+"_MONTH");
    			int j = 1;
    			while(resultSet.next()) {
    			     row = spreadsheet.createRow(j);
    			     cell = row.createCell(0);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_EHIER_CD"));
    			     cell = row.createCell(1);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_EHIER_PARCD"));
    			     cell = row.createCell(2);
    			     cell.setCellValue(resultSet.getString(currMonth2+"_MONTH"));
    			     cell = row.createCell(3);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_EHIER_CD"));
    			     cell = row.createCell(4);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_EHIER_PARCD"));
    			     cell = row.createCell(5);
    			     cell.setCellValue(resultSet.getString(prevMonth2+"_MONTH"));
    			     j++;
    		}
    			spreadsheet.autoSizeColumn(0);
        		spreadsheet.autoSizeColumn(1);
        		spreadsheet.autoSizeColumn(2);
        		spreadsheet.autoSizeColumn(3);
        		spreadsheet.autoSizeColumn(4);
        		spreadsheet.autoSizeColumn(5);
            }
    		POIXMLProperties xmlProps = workbook.getProperties();    
			POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
			coreProps.setCreator("Shubham Shah");	
			coreProps.setDescription("ESS - Differential Report ");
    		FileOutputStream out = null;
			 new File(filePath+"\\"+fileName).mkdirs();
			 out = new FileOutputStream(new File(filePath+"\\"+fileName+"\\"+fileName+"-"+fileOwner+".xlsx"));
			 workbook.write(out);
			 out.close();
			 System.out.println("File written successfully");
    			 
    		} catch (SQLException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}

	/***
	 * 
	 * @param ConfigParameter
	 * @return the value present the Configuration Document
	 */
	public static  String readConfig(String ConfigParameter) {
		Properties props = new Properties();
		String fileName = "Config.Properties";
		String value = null;

		try {
			File file;
			File f = new File(filePath+"\\Config.Properties");
			System.out.println(f);
			props.load(new FileInputStream(f));
			//props.load(RemdeyLogin.class.getClassLoader().getResourceAsStream(ConfigParameter));
			value = props.getProperty(ConfigParameter, String.valueOf(ConfigParameter));
		} catch (IOException ioe) {
			System.err.println("I/O Exception on loading " + fileName + " file:\n" + ioe.getMessage());
			System.exit(1);
		} 
		return value;
	}
	
	/***
	 * 
	 * @param year
	 * @param currmonth
	 * @param prevmonth
	 * @param currMonth
	 * @param prevMonth
	 * @param division
	 * @return Generated Query
	 */
	public static String createQuery(int year,int currmonth,int prevmonth,String currMonth,String prevMonth,String division,int type )
	{
		String query=null;
		switch(type)
		{
		
		case 1:
			query ="SELECT "+currMonth+"_EHIER_CD, "+currMonth+"_EHIER_PARCD, "+currMonth+"_MONTH, "+prevMonth+"_EHIER_CD, "+prevMonth+"_EHIER_PARCD, "+prevMonth+"_MONTH "+
        		" FROM "+ 
        		" ( ( SELECT EHIER_CD "+currMonth+"_EHIER_CD, EHIER_PARCD "+currMonth+"_EHIER_PARCD, '"+currMonth+"' "+currMonth+"_MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_EHIER_PARCD, NULL "+prevMonth+"_MONTH FROM HIERARCHY "+
        		" WHERE YEAR =  "+year+
        		" AND MONTH =  "+currmonth+
        		" AND EHIER_DIVISION = '"+division+"'"+
        		" EXCEPT "+
        		" SELECT EHIER_CD "+currMonth+"_EHIER_CD, EHIER_PARCD "+currMonth+"_EHIER_PARCD, '"+currMonth+"' "+currMonth+"_MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_EHIER_PARCD, NULL "+prevMonth+"_MONTH FROM HIERARCHY "+
        		" WHERE YEAR = "+year+
        		" AND MONTH =  "+prevmonth+
        		" AND EHIER_DIVISION =  '"+division+"')"+
        		" UNION ALL "+
        		" ( SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_EHIER_PARCD, NULL "+currMonth+"_MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, EHIER_PARCD "+prevMonth+"_EHIER_PARCD, '"+prevMonth+"' "+prevMonth+"_MONTH FROM HIERARCHY "+
        		" WHERE YEAR =  "+year+
        		" AND MONTH =  "+prevmonth+
        		" AND EHIER_DIVISION = '"+division+"'"+
        		" EXCEPT "+
        		" SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_EHIER_PARCD, NULL "+currMonth+"_MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, EHIER_PARCD "+prevMonth+"_EHIER_PARCD,  '"+prevMonth+"' "+prevMonth+"_MONTH FROM HIERARCHY "+
        		" WHERE YEAR = "+year+
        		" AND MONTH =  "+currmonth+
        		" AND EHIER_DIVISION = '"+division+"'"+
        		" )) AS A";
		System.out.println("\nQuery 1"+query);
		break;
			
		case 2:
			query = " SELECT "+currMonth+"_EHIER_CD, "+currMonth+"_POOL_CODE, "+currMonth+"__MONTH, "+prevMonth+"_EHIER_CD, "+prevMonth+"_POOL_CODE, "+prevMonth+"_MONTH "+
					" FROM "+
					"(( SELECT EHIER_CD "+currMonth+"_EHIER_CD, POOL_CODE "+currMonth+"_POOL_CODE, '"+currMonth+"' "+currMonth+"__MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_POOL_CODE, NULL "+prevMonth+"_MONTH  "+
					" FROM HIERARCHY_DETAIL"+
					" WHERE YEAR = "+year+
					" AND MONTH = "+currmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+currmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" EXCEPT "+
					" SELECT EHIER_CD "+currMonth+"_EHIER_CD, POOL_CODE "+currMonth+"_POOL_CODE, '"+currMonth+"' "+currMonth+"__MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_POOL_CODE, NULL "+prevMonth+"_MONTH "+
					" FROM HIERARCHY_DETAIL "+
					" WHERE YEAR = "+year+
					" AND MONTH = "+prevmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+prevmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" )UNION ALL ( "+
					" SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_POOL_CODE, NULL "+currMonth+"__MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, POOL_CODE "+prevMonth+"_POOL_CODE, '"+prevMonth+"' "+prevMonth+"_MONTH "+
					" FROM HIERARCHY_DETAIL "+
					" WHERE YEAR = "+year+
					" AND MONTH = "+prevmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+prevmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" EXCEPT "+
					" SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_POOL_CODE, NULL "+currMonth+"__MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, POOL_CODE "+prevMonth+"_POOL_CODE, '"+prevMonth+"' "+prevMonth+"_MONTH "+
					" FROM HIERARCHY_DETAIL "+
					" WHERE YEAR = "+year+
					" AND MONTH = "+currmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+currmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" )) AS A";
			System.out.println("\nQuery 2"+query);
			break;
		case 3:
			query =" SELECT "+currMonth+"_EHIER_CD, "+currMonth+"_STOCKIST_CODE, "+currMonth+"_MONTH, "+prevMonth+"_EHIER_CD, "+prevMonth+"_STOCKIST_CODE, "+prevMonth+"_MONTH "+
					" FROM "+ 
					"((SELECT EHIER_CD "+currMonth+"_EHIER_CD, STOCKIST_CODE "+currMonth+"_STOCKIST_CODE, '"+currMonth+"' "+currMonth+"_MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_STOCKIST_CODE, NULL "+prevMonth+"_MONTH "+
					" FROM STOCKIST_TERR_MAPPING"+
					" WHERE YEAR = "+year+
					" AND MONTH ="+currmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH ="+currmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" EXCEPT "+
					" SELECT EHIER_CD "+currMonth+"_EHIER_CD, STOCKIST_CODE "+currMonth+"_STOCKIST_CODE, '"+currMonth+"' "+currMonth+"_MONTH, NULL "+prevMonth+"_EHIER_CD, NULL "+prevMonth+"_STOCKIST_CODE, NULL "+prevMonth+"_MONTH "+
					" FROM STOCKIST_TERR_MAPPING "+
					" WHERE YEAR = "+year+
					" AND MONTH = "+prevmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+prevmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" )UNION ALL( "+
					" SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_STOCKIST_CODE, NULL "+currMonth+"_MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, STOCKIST_CODE "+prevMonth+"_STOCKIST_CODE, '"+prevMonth+"' "+prevMonth+"_MONTH "+ 
					" FROM STOCKIST_TERR_MAPPING "+
					" WHERE YEAR = "+year+
					" AND MONTH = "+prevmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH = "+prevmonth+" AND EHIER_DIVISION = '"+division+"')"+
					" EXCEPT "+
					" SELECT NULL "+currMonth+"_EHIER_CD, NULL "+currMonth+"_STOCKIST_CODE, NULL "+currMonth+"_MONTH, EHIER_CD "+prevMonth+"_EHIER_CD, STOCKIST_CODE "+prevMonth+"_STOCKIST_CODE, '"+prevMonth+"' "+prevMonth+"_MONTH "+ 
					" FROM STOCKIST_TERR_MAPPING "+
					" WHERE YEAR = "+year+
					" AND MONTH ="+currmonth+
					" AND EHIER_CD IN (SELECT EHIER_CD FROM HIERARCHY WHERE YEAR = "+year+" AND MONTH ="+currmonth+" AND EHIER_DIVISION = '"+division+"')"+
					")) AS A";
			System.out.println("\nQuery 3"+query);
			break;
			default:System.out.println("Wrong Type entered");
					System.exit(0);
		}
		return query;
	}

}
