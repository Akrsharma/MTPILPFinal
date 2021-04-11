import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


class Machine
{
    public int TC; 
    public int CPPC;
    public double PECRPC;
    public double CECRPC;
    public double ECPUEC;
}



class Task
{
    public int TI;
    public int ON;
    public int D;
    public double R;
    public int TCS;
    public int DS; 
}


class Ilpcodefile2 {
    static int N;                //No of machines                  
    static int TID;
    static int TI;
    static int TS;
    static double BW = 100;
    public static void main(String[] args) throws FileNotFoundException, IOException{
        
        String path = "../Phase2/codepart3/";
        Scanner sc = new Scanner(new File(path+"config"));
        String array[];
        int a = 0;
        while(sc.hasNextLine())
        {
            array = sc.nextLine().split(" ");
            if(a == 0)
                N = Integer.parseInt(array[array.length-1]);
            else if(a == 2)
                TID = Integer.parseInt(array[array.length-1]);
            else if(a == 3)
                TI = Integer.parseInt(array[array.length-1]);
            else if(a == 5)
                TS = Integer.parseInt(array[array.length-1]);
            

            a++;
        }
        sc.close();
        
        ArrayList<Machine> machines = new ArrayList<>();
        sc = new Scanner(new File(path+"machineDetails.txt"));
        Machine machine;
        while(sc.hasNextLine())
        {
            array = sc.nextLine().split(" ");
            machine = new Machine();
            machine.TC = Integer.parseInt(array[0]);
            machine.CPPC = Integer.parseInt(array[1]);
            machine.PECRPC = Double.parseDouble(array[2]);
            machine.CECRPC = Double.parseDouble(array[3]);
            machine.ECPUEC  = Double.parseDouble(array[4]);
            machines.add(machine);
        }
        sc.close();
        //System.out.println(machines.size());
        int ts = 1;
        String filename;
        while(ts <= TS)
        {
            ArrayList<Task> tasks = new ArrayList<>();
            ArrayList<ArrayList<Double>> pijx = new ArrayList<>();
            ArrayList<ArrayList<Double>> pijy = new ArrayList<>();
            ArrayList<ArrayList<Double>> pijz = new ArrayList<>();

            sc = new Scanner(new File(path+"taskDetails"+ts+".txt"));
            Task task;
            while(sc.hasNextLine())
            {

                array = sc.nextLine().split(" ");
                task = new Task();
                task.TI = Integer.parseInt(array[0]);
                task.ON = Integer.parseInt(array[1]);
                task.D = Integer.parseInt(array[2]);
                task.TCS = Integer.parseInt(array[3]);
                task.DS = Integer.parseInt(array[4]);
                task.R = Double.parseDouble(array[5]);
                tasks.add(task);
            }

            sc.close();
            //System.out.println(tasks.size());
            ArrayList<Double> tmp1, tmp2, tmp3;
            double profit, cost;
            //System.out.println(tasks.size());
            for(int i=0; i<machines.size(); i++)
            {
                tmp1 = new ArrayList<>();
                tmp2 = new ArrayList<>();
                tmp3 = new ArrayList<>();
                for(int j=0; j<tasks.size(); j++)
                {
                    machine = machines.get(i);
                    task = tasks.get(j);
                    if(task.ON == i)
                    {
                        cost  = ((task.TCS * 1.0)/machine.CPPC) * machine.PECRPC * machine.ECPUEC;
                        profit = task.R - cost;
                        if(profit < 0 )
                            System.out.println("Profit for xij = 1: "+profit);
                        tmp1.add(profit);


                        cost = 0;
                        profit = 0.1 * task.R - cost;
                        tmp2.add(profit);

                        cost = ((task.TCS * 1.0)/machine.CPPC * machine.PECRPC + (task.DS/100.0) * machine.CECRPC)*machine.ECPUEC;
                        
                        profit = 0.9*task.R - cost;
                        if(profit < 0 )
                            System.out.println("Profit for zij = 1: "+profit);
                        tmp3.add(profit);
                    }
                    else
                    {
                        tmp1.add(0.0);
                       
                        cost = 0;
                        profit = 0.1 * task.R - cost;
                        tmp2.add(profit);
                        //System.out.println(task.TCS + " "+ machine.CPPC + " "+ machine.PECRPC + " " + task.DS + " " + 
                        //machine.CECRPC + " " + machine.ECPUEC);
                        cost = ((task.TCS * 1.0)/machine.CPPC * machine.PECRPC + (task.DS/100.0) * machine.CECRPC)*machine.ECPUEC;
                        
                        profit = 0.9*task.R - cost;
                        if(profit < 0 )
                            System.out.println("Profit for zij = 1: "+profit);
                        tmp3.add(profit);
                    }
                }
                //System.out.println(tmp1.size()+ " "+ tmp2.size()+ " "+ tmp3.size());
                pijx.add(tmp1);
                pijy.add(tmp2);
                pijz.add(tmp3);
            }
            filename = "TaskILP1" + ts+".lp";
            FileWriter fw = new FileWriter(filename);
            fw.write("max: \n\t");
            //System.out.println("Total machines are : "+machines.size());

            //fw.write("\n\tobj: ");
            for(int i=0; i<machines.size(); i++)
            {
                //System.out.println(tasks.size());
                for(int j=0; j<tasks.size(); j++)
                {
                    if(j != 0 && j%5 == 0)
                        fw.write("\n\t     ");
                    if(pijx.get(i).get(j) != 0.0)
                    {
                        fw.write("x_"+i+j+" "+pijx.get(i).get(j) + " + ");
                    }
                    if(i == machines.size()-1 && j == tasks.size()-1)
                        fw.write("y_"+i+j+" "+pijy.get(i).get(j) + " + " + "z_"+i+j+" "+pijz.get(i).get(j)+";");
                    else
                        fw.write("y_"+i+j+" "+pijy.get(i).get(j) + " + " + "z_"+i+j+" "+pijz.get(i).get(j)+" + ");

                }

            }
            fw.write("\n\n");
            //fw.write("\nSubject To\n\n");




            for(int i=0; i<machines.size(); i++)
            {
                for(int j=0; j<tasks.size(); j++)
                {
                    fw.write("\t");
                    if(pijx.get(i).get(j) != 0.0)
                    {
                        fw.write("x_"+i+j+" + ");
                    }
                    fw.write("y_"+i+j+" + "+"z_"+i+j+" <= 1;\n");
                }
            }
            fw.write("\n\n\n");

            for(int j=0; j<tasks.size(); j++)
            {
                fw.write("\t");
                for(int i=0; i<machines.size(); i++)
                {
                    if(i != machines.size()-1)
                        fw.write("y_"+i+j+" + ");
                    else
                        fw.write("y_"+i+j+" <= 1;\n");
                }
                
            }
            fw.write("\n\n\n");

            for(int j=0; j<tasks.size(); j++)
            {
                fw.write("\t");
                for(int i=0; i<machines.size(); i++)
                {
                    if(i != machines.size()-1)
                        fw.write("z_"+i+j+" + ");
                    else
                        fw.write("z_"+i+j+" <= 1;\n");
                }
                
            }
            fw.write("\n\n\n");
            double exec1 = 0, exec2 = 0;
            int taskno = 0, t, j = 0, i;
            //System.out.println(TI);
            for(t=0; t<TI; t++);
            {
                //System.out.println(t + " " + TI);
                int tmp = 0;
                for(i=0; i<machines.size(); i++)
                {
                    //int count = 0;

                    for(j = 0; j<tasks.size() && tasks.get(j).TI == t; j++)
                    {
                        //count++;
                        exec1 = ((tasks.get(j).TCS) * 1.0)/machines.get(i).CPPC;
                        exec2 = ((tasks.get(j).TCS) * 1.0)/machines.get(i).CPPC;
                        if(j == taskno)
                        {
                            if(tasks.get(j).ON == i)
                            {
                                fw.write("x_"+i+j+" "+exec1 + " + ");
                            }
                            fw.write("z_"+i+j+" "+exec2 );
                            tmp = tasks.get(j).D;
                        }
                        else
                        {
                            if(tasks.get(j).ON == i)
                            {
                                fw.write(" + " + "x_"+i+j+" "+exec1);
                            }
                            fw.write(" + " + "z_"+i+j+" "+exec2 );
                        }
                        
                    }
                    fw.write(" <= "+ (tmp - (t-1)*TID) + ";\n");
                    //System.out.println(count);

                }
                fw.write("\n\n");
            }
            fw.write("\n\n");
            fw.write("bin\n\n");
            for(i=0; i<machines.size(); i++)
            {
                for(j=0; j<tasks.size(); j++)
                {
                    if(j%10 == 0)
                        fw.write("\n\t");
                    if(pijx.get(i).get(j) != 0.0)
                    {
                        fw.write("x_"+i+j+ " ");
                    }
                    
                    fw.write("y_"+i+j+ " " + "z_"+i+j+" ");
                    
                }

            }
            fw.write(";");

            fw.close();

            ts++;
        }
        


    }
}
