/*
 * Printing Neatly - Design and Analysis of Computer Algorithms Project
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class PrintingNeatly
{
	static ArrayList<String> total_words=new ArrayList<String>();
	static int M = 72;									// Default value of M
	static int infinity = 2000000000;
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner in;
		File inputFile;
	
		if (args.length > 0) 
		{
		    inputFile = new File(args[0]);
		    if (args.length > 1) 
		    { 
		    	M = Integer.parseInt(args[1]);
		    }
		    in = new Scanner(new FileReader(inputFile));
		    while (in.hasNext())
		    {
		    	String s=in.next();
		    	if(s.equals("(b)"))    // End of paragraph
		    		break;
		    	total_words.add(s);
		    	if(s.length()>M)       //Constraint : where the given word length is greater than the value of M
		    	{
		    		System.out.println("One of the word's length is greater than line size...program terminated");
		    		System.exit(0);
		    	}
		    }
		}
		 else 
		 {
			    in = new Scanner(System.in);
			    while(in.hasNext())
			    {  	
			    	String s=in.next();
			    	if(s.equals("(b)"))
			    		break;
			    	total_words.add(s);
			    	if(s.length()>M)
			    	{
			    		System.out.println("One of the word's length is greater than line size...program terminated");
			    		System.exit(0);
			    	}
			    }
		 }
		
		int[] words_length=new int[total_words.size()];
		for(int i=0;i<total_words.size();i++)
		{
			words_length[i]=total_words.get(i).length();
		}
		cost_calculation(words_length,M);
	}
	
	/* A method to Calculate cost for all combinations of the words in a single line. 
	 * Passing the word lengths and the Value M as the parameter to the method.
	 */
	public static void cost_calculation(int[] words,int l)
	{
		int[][] cost=new int[words.length][words.length];
		
		for(int i=0;i<words.length;i++)
		{
			for(int j=0;j<words.length;j++)
			{
				cost[i][j]=infinity;			// Initializing the cost matrix to infinity.
			}
		}
		for(int i=0;i<words.length;i++)
		{
			int current_cost = words[i];
			cost[i][i]=l-current_cost;
			if(i==words.length-1)					// When a sentence contains the last word
			{
				cost[i][i]=0;
			}
			for(int j=i+1;j<words.length ;j++)
			{
				if(current_cost+words[j]+1<l)		//Checking if the number of characters equals the value M
				{	
					current_cost+=words[j]+1;
					cost[i][j]=l-current_cost;
					if(j==words.length-1)
					{
						cost[i][j]=0;
						continue;
					}
					continue;
				}
				else if(current_cost+1+words[j]==l)		// When the number of characters is exactly equal to M
				{
					current_cost+=words[j]+1;
					cost[i][j]=0;				//l-current_cost;  No cost incurred.
					continue;
				}
				current_cost=current_cost+1+words[j];		
			}
		}
		penaltyCalculation(cost,words);
	}
	
	/* A Method to calculate the penalty for the all the combination of words.
	 * Passing the cost matrix and the length of the words as a parameter to the method
	 */
	public static void penaltyCalculation(int[][] cost,int[] words)
	{
		int[][] penalty=new int[words.length][words.length];
		for(int i=0;i<words.length;i++)
		{
			for(int j=0;j<words.length;j++)
			{
				penalty[i][j] = 0;						// Initializing the penalty matrix
		
			}
		}
		for(int i=0;i<words.length;i++)
		{
			for(int j=0;j<words.length;j++)
			{
				if(penalty[i][j]!=infinity)
					penalty[i][j]=cost[i][j]*cost[i][j]*cost[i][j];		//Calculating penalty for every combination of words in a line
			}
		}
		totalPenalty(words,penalty);
	}
	
	/* A method to calculate the total penalty i.e., the optimal solution out
	 *  of all the combinations.
	 *  Passing the length of words and the penalty matrix as arguments to the method
	 */
	public static void totalPenalty(int[] words,int[][] penalty)
	{	
		int[] totalPenalty=new int[words.length+1];
		int[] cut=new int[words.length+1];
		totalPenalty[0] = 0;
		for (int j = 0; j < words.length; j++)
	    {
	        totalPenalty[j+1] = infinity;
	        for (int i = 0; i <= j; i++)
	        {
	            if (totalPenalty[i] != infinity && penalty[i][j] != infinity && (totalPenalty[i] + penalty[i][j] < totalPenalty[j+1]))		// Selecting the optimal solution 
	            {
	                totalPenalty[j+1] = totalPenalty[i] + penalty[i][j];   // Penalty till the jth word
	                cut[j+1] = i;										   // Position where the line has to break
	            }
	        }
	    }
		prettyPrinting(words,cut,totalPenalty);
	}
		
	/* A method to Print the given input neatly according to the optimal solution.
	 * Sending the length of words, the cut positions and the total penatly as parameter to the method
	 */
	public static void prettyPrinting(int[] words,int[] cut,int[] totalPenalty)
	{
		ArrayList<Integer> cut_position = new ArrayList<Integer>();
		int n = words.length;
		int extra;
		int k=0;
		int temp=0;
		int words_printed_inline=0;
		int first_word=0;
		while(n>0)
		{
			cut_position.add(cut[n]);
			n=cut[n];
		}
		
		System.out.println(totalPenalty[cut.length-1]);    //Printing the total penalty for the given input
		System.out.println();
		
		//*********Printing neatly****************
		for(int c=cut_position.size()-2;c>=0;c--)
		{
			boolean flag=false;
			int wordlength = 0,spaces,extraspace,num_words;
			spaces=cut_position.get(c)-1-temp;
			while(temp<cut_position.get(c))
			{
				wordlength+=words[temp];
				temp++;
			}
			extraspace=M-wordlength-spaces;
			num_words=spaces+1;
			extra = (num_words/2);
			if((extraspace!=0) && (extraspace<extra))
				flag=true;
			while(k<cut_position.get(c))
			{
				words_printed_inline=words_printed_inline+total_words.get(k).length(); //no. of characters printed in the line till now
				
				if(M-words_printed_inline >0 && k+1>=cut_position.get(c) && extraspace>0 && first_word==1) //last word
				{
					while(extraspace>0)
					{
						System.out.print("+");
						extraspace--;
						words_printed_inline++;
						
					}
					System.out.print(total_words.get(k));
				}
				else if(M-words_printed_inline >0)
				{
            				System.out.print(total_words.get(k)+" ");
            				first_word=1;
            				words_printed_inline++;
				}
				else if(M-words_printed_inline ==0)
				{
							System.out.print(total_words.get(k));
							first_word=1;
				}
				
				if(extraspace>0 && M-words_printed_inline>0)
				{
					if(flag == true)
					{
						if(k%2==0)
						{
							System.out.print("+");
							words_printed_inline++;
							extraspace--;
						}
					}
					else  if(flag == false)
					{
						System.out.print("+");
						words_printed_inline++;
						extraspace--;
					}
				}
				k++;
			}
			k=cut_position.get(c);
			temp=k;
			System.out.println();
			first_word=0;
			words_printed_inline=0;
		}
		while(k<total_words.size())
		{
			words_printed_inline=words_printed_inline+total_words.get(k).length();
			if(M-words_printed_inline>0)
			{
				System.out.print(total_words.get(k)+" ");
			}
			k++;
		}
	}
}
