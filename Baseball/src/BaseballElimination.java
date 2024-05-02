import java.util.HashMap;
import java.util.Scanner;

public class BaseballElimination
{
	private int[][] games;
	private int[] win;
	private int[] loss;
	private int[] remaining;
	private int vertices;
	
	private int totalMatches;
	
	private int firstTeamVertex;
	
	private boolean flag;
	
	//private Team[] teams;
	private HashMap<Integer, String> teamNames;	//O(1) lookup for wins(), etc.
	private HashMap<String, Integer> teamNumbers;
	/*
	private class Team {
		private String nam;
		private int num;
		public Team(String na, int nu) {
			nam = na;
			num = nu;
		}
		public String name() {
			return nam;
		}
		public int number() {
			return num;
		}
	}
	*/
	
	
	
	
	public BaseballElimination(String filename) {
		In in = new In(filename);
		
		// USE THIS SOMEHOW MAYBE: Count of teams:
		int cTeams = Integer.parseInt(in.readLine());
		
		games = new int[cTeams][cTeams];
		win = new int[cTeams];
		loss = new int[cTeams];
		remaining = new int[cTeams];
		vertices = -1;
		totalMatches = -1;
		flag = false;
		firstTeamVertex = -1;
		
		//teams = new Team[cTeams];
		teamNames = new HashMap<Integer, String>();
		teamNumbers = new HashMap<String, Integer>();
		
		
		for (int iTeam = 0; iTeam < cTeams; iTeam++) {
			String line = in.readLine();
			Scanner lineScanner = new Scanner(line);
			
			// USE THIS SOMEHOW MAYBE: Name of team
				//teams[iTeam] = new Team(lineScanner.next(), iTeam);
			String str = lineScanner.next();
			teamNames.put(iTeam, str);
			teamNumbers.put(str, iTeam);
			
			// USE THIS SOMEHOW MAYBE: Number of wins for the team
			win[iTeam] = lineScanner.nextInt();
			
			// USE THIS SOMEHOW MAYBE: Number of losses for the team
			loss[iTeam] = lineScanner.nextInt();
			
			// USE THIS SOMEHOW MAYBE: Number of total remaining games for the team
			remaining[iTeam] = lineScanner.nextInt();
			
			for (int iAgainst = 0; iAgainst < cTeams; iAgainst++)
			{
				// USE THIS SOMEHOW MAYBE: Number of games remaining between iTeam and iAgainst
				games[iTeam][iAgainst] = lineScanner.nextInt();
			}
			lineScanner.close();
		}
	}

	public int numberOfTeams() {
		return win.length;
	}

	public Iterable<String> teams() {
		SET<String> set = new SET<String>();
		for(int i = 0; i < numberOfTeams(); i++)
				//set.add(teams[i].name());
			set.add(teamNames.get(i));
		return set;
	}

	public int wins(String team) {
		if(teamNumbers.get(team) == null)
			throw new java.lang.IllegalArgumentException();
		
		int teamNo = teamNumbers.get(team);
		return win[teamNo];
	}

	//repeat wins();
	public int losses(String team) {
		if(teamNumbers.get(team) == null)
			throw new java.lang.IllegalArgumentException();
		
		int teamNo = teamNumbers.get(team);
		return loss[teamNo];
	}

	//repeat wins();
	public int remaining(String team) {
		if(teamNumbers.get(team) == null)
			throw new java.lang.IllegalArgumentException();
		
		int teamNo = teamNumbers.get(team);
		return remaining[teamNo];
	}

	//repeat wins();
	//use games[team1][team2];
	public int against(String team1, String team2) {
		if(teamNumbers.get(team1) == null || teamNumbers.get(team2) == null)
			throw new java.lang.IllegalArgumentException();
		
		int teamNo1 = teamNumbers.get(team1);
		int teamNo2 = teamNumbers.get(team2);
		return games[teamNo1][teamNo2];
	}
	
	private FlowNetwork network(String team) {
		
		//create a flowNetwork
		//return new FordFulkerson(flowNetwork, s, t);
		
		int curTeam = teamNumbers.get(team);
		
		//i. 	envision the games vertices grid, and the "diagonal split" through the middle
		//ii. 	add up all the 'vertices' on the bottom left
		//iii.	factor out the team you're testing
		//iv.	matchups between teams
		
		//v.	team vertices
		//vi. 	s & t
		
		//vii.	remaining matches & team vertices
		//viii.	remaining matches vertices
		//ix.	team vertices
		
		//x.	edges to t
		//xi.	(win[a] + remaining[a]) - win[b]
		
		//i. ii.
		int matchups = 0;
		totalMatches = 0;
		for(int i = 0; i < numberOfTeams(); i++) {
			for(int j = 0; j < i; j++) {
				if(i != curTeam && j != curTeam)	//iii.
					totalMatches += games[i][j];
					matchups++;	//iv.
				}
		}
		
		vertices = matchups + numberOfTeams()-1;	//v.
		vertices +=2;	//vi.
		FlowNetwork flowNetwork = new FlowNetwork(vertices);
		int s = 0;
		int t = vertices-1;
		
		//to be used in ix.
		firstTeamVertex = 1 + matchups;
		//
		
		//vii.
		int teamA = 0;
		int vert = 1;
		int x = numberOfTeams()-1;
		
		while(x > 0) {
			for(int i = 1; i <= x; i++) {
				int teamB = teamA + i;
				if(teamA != curTeam && teamB != curTeam) {
					//viii.
					flowNetwork.addEdge(new FlowEdge(s, vert, games[teamA][teamB]));
					
					//ix.
					int teamAVertex = firstTeamVertex + teamA;
					int teamBVertex = firstTeamVertex + teamB;
					flowNetwork.addEdge(new FlowEdge(vert, teamAVertex, Integer.MAX_VALUE));
					flowNetwork.addEdge(new FlowEdge(vert, teamBVertex, Integer.MAX_VALUE));
					
					vert++;
				}
			}
			x--;
			teamA++;
		}
		
		//x.
		for(int i = 0; i < numberOfTeams(); i++) {
			if(i != curTeam) {
				//xi.
				int capacity = (win[curTeam] + remaining[curTeam]) - win[i];	//TODO: is there a negative capacity condition?
				//if(capacity < 0)
					//flag = true;
				//else
					flowNetwork.addEdge(new FlowEdge(firstTeamVertex+i, t, capacity));
			}
		}
		
		return flowNetwork;
	}
	
	private boolean isTrivElim(String team) {
		int curTeam = teamNumbers.get(team);
		for(int i = 0; i < numberOfTeams(); i++) {
			if(i != curTeam && (win[curTeam] + remaining[curTeam] < win[i]) )
				return true;
		}
		return false;
	}
	

	public boolean isEliminated(String team) {
		if(teamNumbers.get(team) == null)
			throw new java.lang.IllegalArgumentException();
		
		if(isTrivElim(team)) {	//nontrivial elim TODO: maybe this doesn't work.
			return true;
		}
		
		FlowNetwork flowNetwork = network(team);
		
		FordFulkerson maxFlow = new FordFulkerson(flowNetwork, 0, vertices-1);
		
		int fires = 0;	//total amount of matches left
		for(int i = 0; i < numberOfTeams(); i++) {
			for(int j = 0; j < i; j++) {
				//if(i != teamNumbers.get(team))
				if(i != teamNumbers.get(team) && j != teamNumbers.get(team))
					fires += games[i][j];
				//System.out.println(teamNames.get(i) + " " + teamNames.get(j));
				//System.out.println(games[i][j]);
			}
		}
		
		//TODO: i know why it's broken sometimes! need to find whether all flows from s are full
		
		
		
		
		System.out.println(team + " | " + maxFlow.value() + " | " + totalMatches);
		//if(maxFlow.value() < totalMatches)
		if(maxFlow.value() < fires)
			return true;
		return false;
	}

	//use mincut?
	
	//TODO: have to create certOfElim for nontrivial eliminations (isTrivElim())
	public Iterable<String> certificateOfElimination(String team) {
		if(teamNumbers.get(team) == null)
			throw new java.lang.IllegalArgumentException();
		
		if(!isEliminated(team))
			return null;
		
		if(isTrivElim(team)) {
			return certOfTrivElim(team);
		}
		
		int curTeam = teamNumbers.get(team);
		
		FlowNetwork flowNetwork = network(team);
		FordFulkerson maxFlow = new FordFulkerson(flowNetwork, 0, vertices-1);
		SET<String> set = new SET<String>();
		
		for(int i = 0; i < numberOfTeams(); i++) {
			if(i != curTeam) {
				if(maxFlow.inCut(firstTeamVertex+i)) {
					set.add(teamNames.get(i));
				}
			}
		}
		
		return set;
	}
	
	private Iterable<String> certOfTrivElim(String team) {
		int curTeam = teamNumbers.get(team);
		
		SET<String> set = new SET<String>();
		//for each team with more wins than curTeam
		for(int i = 0; i < numberOfTeams(); i++) {
			if(i != curTeam && (win[curTeam] + remaining[curTeam] < win[i]) ) {
				set.add(teamNames.get(i));
				//temp for test
				return set;
			}
		}
		return set;
	}
	
	
	public static void main(String[] args) 
	{
        BaseballElimination division = new BaseballElimination("testInput/teams4a.txt");
        for (String team : division.teams()) 
        {
            if (division.isEliminated(team)) 
            {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else 
            {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
