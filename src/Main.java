import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/*
 * Chemistry Shell
 * So as to simplify uploads, this will stay in one file.
 */

/*
 * Data credits:
 * 
 * Element and Polyatomic Ion files: www.sunjay.ca 
 * Compound information: Wikipedia
 * 
 */

public class Main{
	
	//File Information
	private static String elementAddress = "periodic_table.csv";
	private static final String polyatomicAddress = "polyatomics.csv";
	private static final String formulaAddress = "formulas.txt";
	
	//Element Information
	private static ArrayList<Integer> EleNumbers = new ArrayList<Integer>();
	private static ArrayList<Double> EleMasses = new ArrayList<Double>();
	private static ArrayList<String> EleNames = new ArrayList<String>();
	private static ArrayList<String> EleAbbrevs = new ArrayList<String>();
	
	//Polyatomic Ion Information
	private static ArrayList<String> IonNames = new ArrayList<String>();
	private static ArrayList<String> IonFormulas = new ArrayList<String>();
	private static ArrayList<Integer> IonCharges = new ArrayList<Integer>();
	
	//Compound Information
	private static HashMap<String,String> NamesToFormulas = new HashMap<String,String>();
	
	//Load Information
	private static void loadFiles() throws FileNotFoundException{
		
		//Elements
		Scanner scan = new Scanner(new File(elementAddress));
		int count = 0;//Keep track of which arraylist to add to
		while(scan.hasNextLine()){
			Scanner line = new Scanner(scan.nextLine());
			line.useDelimiter(",");
			while(line.hasNext()){
				switch(count){
				case 0:
					EleNumbers.add(Integer.parseInt(line.next()));
					break;
				case 1:
					EleMasses.add(Double.parseDouble(line.next()));
					break;
				case 2:
					EleNames.add(line.next());
					break;
				case 3:
					EleAbbrevs.add(line.next());
					break;
				}
				count++;
				count%=4;
			}
		}
	}

	//Main
	public static void main(String[] args) throws Exception{//Yolo
		System.out.println(">>> ChemShell Beta Version 0.1");
		System.out.println(">>> By Jormungandr Studios");
		System.out.println(">>> Type 'help' for Command List");
		System.out.println(">>>");
		if(args.length>0)
			elementAddress = args[0];
		loadFiles();
		while(true){
			System.out.print(">>> ");
			processLine((new Scanner(System.in)));
		}
	}
	
	//Primary commands
	private static final String[] primaries = {
		"MOLES",
		"GRAMS",
		"GRAMSPERMOLE",
		"FORMULA",
		"HELP"
	};
	
	//Line Processing
	private static void processLine(Scanner input){
		 int primary = Arrays.asList(primaries).indexOf(((input).next().toUpperCase()));
		 switch(primary){
		 case -1://Invalid
			 System.out.println("> ERROR: Invalid Primary Command");
			 return;
		 case 0://Moles
			 toMoles(input);
			 return;
		 case 1://Grams
			 toGrams(input);
			 return;
		 case 2://Grams per Mole
			 System.out.println(gramsPerMole(chemFormula(input.nextLine()))+" g/mole");
			 return;
		 case 3://Formula
			 System.out.println(chemFormula(input.nextLine()));
			 return;
		 case 4:
			 getHelp();
			 return;
		 case 5:
			 balance(input.nextLine());
			 return;
		 }
	}
	
	/*
	 * Science!!!
	 */
	
	//Parse Chemical Formula from name
	private static String chemFormula(String name){
		
		return name;
		
		/*
		String out = NamesToFormulas.get(name.toLowerCase());
		System.out.println("I think I got it: "+out);
		if (out == null)
			out = name;
		if(out.contains(" ")){
			System.out.println("ERROR: Unrecognized Compound");
			return "X";
		}
		if(!(out.compareTo(name)==0))
			System.out.println("Compound Recognized: "+out);
		return out;
		*/
	}
	
	//Calculate molecular/formula unit weight
	private static double gramsPerMole(String formula){
		double total = 0;
		String abbrev = "";
		int coefficient = 1;
		int multiplier = 1;
		int i = 0;
		String number = "";
		if(formula.charAt(0)==' ')
			formula = formula.substring(1,formula.length());
		while(Character.isDigit(formula.charAt(i))){//Handle leading coefficients
			number+=formula.charAt(i);
			i++;
		}
		if(number.length()>0)
			coefficient = Integer.parseInt(number);
		formula+="X";//Nonexistent element to terminate.
		for(i=i+0;i < formula.length(); i++){
			char c = formula.charAt(i);
			if(Character.isUpperCase(c)){
				total+=atomicMass(abbrev)*multiplier;
				multiplier = 1;
				abbrev = ""+c;
			}
			else if(Character.isDigit(c)){
				number = "";
				while(Character.isDigit(formula.charAt(i))){
						number+=formula.charAt(i);
						i++;
				}
				i--;
				multiplier = Integer.parseInt(number);
			}else{
				abbrev+=c;
			}
		}
		return total*coefficient;
	}
	
	//Get mass of an element
	private static double atomicMass(String abbreviation){
		if(abbreviation.length() == 0)
			return 0;
		if(abbreviation.charAt(0)==' ')
			abbreviation = abbreviation.substring(1,abbreviation.length());
		int index = EleAbbrevs.indexOf((abbreviation));
		if(index != -1){
			return EleMasses.get(index);
		}
		return 0;
	}
	
	//Units for conversions
	private static final String[] units = {//unit, abbrev., grams per 1 unit
		"kilograms","kg","1000",
		"milligrams","mg","0.001",
		"grams", "g", "1"
	};
	
	//Unit Conversion: Mass
	private static double convertMass(String unitIn, String unitOut, double numberIn){
		try{
			if(unitIn.charAt(unitIn.length()-1)!='s'&&unitIn.length()>3)
				unitIn+='s';
			if(unitOut.charAt(unitOut.length()-1)!='s'&&unitOut.length()>3)
				unitOut+='s';
			if(!Arrays.asList(units).contains(unitIn))
				throw new Exception();
			if(!Arrays.asList(units).contains(unitOut))
				throw new Exception();
			double out;
			int index ;
			index = ((Arrays.asList(units).indexOf(unitIn))/3)*3+2;
			out = numberIn*(Double.parseDouble(units[index]));
			index = ((Arrays.asList(units).indexOf(unitOut))/3)*3+2;
			out = out/(Double.parseDouble(units[index]));
			return out;
		} catch(Exception e){
			System.out.println("> ERROR: Invalid Conversion");
			return 0;
		}
	}
	
	//To moles
	private static void toMoles(Scanner input){
		try{
			double amount = input.nextDouble();
			String units = input.next();
			String material = input.next();
			if(units.compareToIgnoreCase("g")*units.compareToIgnoreCase("grams")*units.compareToIgnoreCase("gram")!=0)
				amount = convertMass(units,"grams",amount);//Ensure units are grams
			double moles = amount/(gramsPerMole(chemFormula(material)));
			System.out.println(moles+" moles");
		} catch(Exception e){
			System.out.println("> ERROR: Syntax is \"MOLES [amount] [units] [material]\"");
		}
	}

	
	//To grams
	private static void toGrams(Scanner input){
		try{
			double amount = input.nextDouble();
			String units = input.next();
			String material = input.next();
			if(!units.contains("mole")){
				System.out.println(convertMass(units,"grams",amount)+" grams");
				return;
			}
			double moles = amount*(gramsPerMole(chemFormula(material)));
			System.out.println(moles);
		} catch(Exception e){
			System.out.println("> ERROR: Syntax is \"MOLES [amount] [units] [material]\"");
		}
	}
	
	//Balance an equation
	private static void balance(String str){
		
		//Split products and reagents
		Scanner input = new Scanner(str);
		ArrayList<String> reagents = new ArrayList<String>();
		ArrayList<String> products = new ArrayList<String>();
		boolean flip = false;
		while(input.hasNext()){
			String s = input.next();
			if(s.compareTo("+")==0)
				continue;
			if(s.compareTo("=")==0){
				flip = true;
				continue;
			}
			if(flip)
				products.add(s);
			else
				reagents.add(s);
		}
		while(input.hasNext()){
			products.add(input.next());
		}
		
		str = str.substring(1,str.length());
		str += " ";
		ArrayList<String> elements = new ArrayList<String>();
		String recent = "";
		for(int n = 0; n < str.length(); n++){
			if(Character.isDigit(str.charAt(n))||Character.isWhitespace(str.charAt(n))){
				if(!elements.contains(recent)&&(recent.compareTo("+")*recent.compareTo("=")*recent.compareTo(" ")!=0))
					elements.add(recent);
				recent = "";
			} else if (Character.isUpperCase(str.charAt(n))){
				if(!elements.contains(recent)&&(recent.compareTo("+")*recent.compareTo("=")*recent.compareTo(" ")!=0))
					elements.add(recent);
				recent = "";
				recent+=str.charAt(n);
			} else {
				recent += str.charAt(n);
			}
		}
		
		elements.remove("");
		
		//Yields a list of all elements.
		//Height of matrix
		
		double[][] matrix = new double [elements.size()][(products.size()+reagents.size())];
		//matrix[n][m] means element #n in the arraylist, chemical #m
		//Now to load the values... double check paper
		
		for(int i = 0; i < elements.size(); i++){
			String s = elements.get(i);
			int comp = 1;
			for(comp=comp+0; comp<reagents.size();comp++){
				int ind = comp;
				matrix[i][ind] = -atomsIn(elements.get(i),products.get(ind));
				//step thru compounds/columns
				//comp gives column... i = row...
			}
			for(comp=comp+0; comp<products.size()+reagents.size()-1;comp++){
				int ind = comp-reagents.size();
				matrix[i][ind] = -atomsIn(elements.get(i),reagents.get(ind));
				//Same as above
			}
			int ind = matrix[0].length-1;
			//Same as above
			//Given some compound and some element, find the ele's in the compound
			//Forloop... make a function for this
			matrix[i][ind] = -atomsIn(elements.get(i),reagents.get(0));
			
		}
		
		//Hmm.... how to do this
		//We gots some work to do
		
	}
	
	private static int atomsIn(String element, String compound){
		System.out.println("Looking for "+element+" in "+compound);
		int count = 0;
		
		System.out.println();
		return count;
	}
	
	private static void getHelp(){
		System.out.println("ChemShell Help");
		System.out.println("Primary Commands:");
		System.out.println("	Moles [X] units [Y]");
		System.out.println("	Grams [X] moles [y]");
		System.out.println("	GramsPerMole [X]");
		System.out.println("For instance, \"Moles 2 grams H2SO4\"");
		System.out.println("Note that ChemShell currently supports only chemical formulas.");
	}
	
}
