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
            filename = "TaskILP1" + ts+".lp";
            FileWriter fw = new FileWriter(filename);
            fw.write("max: \n\t");
            Machine helper;
            int c,d = 0;
            for(int i=0; i<machines.size(); i++)
            {

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
                        
                        if(d == 0)
                        {
                            fw.write(profit + "x_"+i+j+" + ");
                            d = 1;
                        }
                        else
                            fw.write("+" + profit + "x_"+i+j+ " + ");
                        c = 0;
                        for(int k=0; k<machines.size(); k++)
                        {
                            if(k == i)
                                continue;
                            profit = 0;
                            helper = machines.get(k);
                            cost = 0;
                            profit = 0.1 * task.R - cost;
                            cost = ((task.TCS * 1.0)/helper.CPPC * helper.PECRPC + (task.DS/100.0) * helper.CECRPC)*helper.ECPUEC;
                            profit += (0.9 * task.R - cost);
                            if(c == 0)
                            {
                                fw.write(""+profit);
                                fw.write("y_"+i+j+k+" ");
                                c = 1;
                            }
                            else
                            {
                                fw.write("+ " + profit +"y_"+i+j+k+" ");
                            }
                            

                        }
                    }
                }

            }

            fw.write(";\n\n");

            for(int i=0; i<machines.size(); i++)
            {
                for(int j=0; j<tasks.size(); j++)
                {
                    machine = machines.get(i);
                    task = tasks.get(j);
                    if(task.ON == i)
                    {
                        fw.write("x_"+i+j + " + ");
                        c = 0;
                        for(int k=0; k<machines.size(); k++)
                        {
                            if(k == i)
                                continue;
                            if(c == 0)
                            {
                                fw.write(" y_"+i+j+k + " ");
                                c=1;
                            }
                            else
                            {
                                fw.write("+ "+"y_"+i+j+k +" ");
                            }

                        }
                        fw.write(" <= 1;");
                        fw.write("\n\n");
                    }
                }
            }
            fw.write("\n\n\n");
            double exec1 = 0, exec2 = 0,sum = 0;
            int taskno = 0, t, j = 0, i;
            for(t=0; t<TI; t++)
            {
                int tmp = 0;
                for(i=0; i<machines.size(); i++)
                {
                    c=0;
                    for(j = 0; j<tasks.size() ; j++)
                    {
                        if(tasks.get(j).TI == t)
                        {
                            tmp = tasks.get(j).D;
                            //System.out.println(tmp);
                            //count++;
                            exec1 = ((tasks.get(j).TCS) * 1.0)/machines.get(i).CPPC;
                            
                            //exec2 = ((tasks.get(j).TCS) * 1.0)/machines.get(i).CPPC;
                            if(c == 0)
                            {
                                sum += exec1;
                                if(tasks.get(j).ON == i)
                                {
                                    fw.write(exec1+"x_"+i+j+" ");
                                }
                                else
                                { 
                                    int k = tasks.get(j).ON;
                                    fw.write(exec1+"y_"+k+j+i+" ");
                                }
                                
                            }
                            else
                            {
                                sum += exec1;
                                if(tasks.get(j).ON == i)
                                {

                                    fw.write(" + " + exec1+"x_"+i+j+" ");

                                }
                                else
                                {
                                    
                                    int k = tasks.get(j).ON;
                                    fw.write(" + " + exec1 + "y_"+k+j+i+" ");
                                }
                            }
                            c = 1;
                        }
                        
                    }
                    //System.out.println(sum);
                    fw.write(" <= " + (tmp - t*TID) + ";\n");
                    //fw.write(" <= "+ "15" + ";\n");
                    //System.out.println(count);

                }
                fw.write("\n\n");
            }
            fw.write("\n\n");
            fw.write("bin  ");
            for(i=0; i<machines.size(); i++)
            {
                for(j=0; j<tasks.size(); j++)
                {
                    if(tasks.get(j).ON == i)
                    {
                        fw.write("x_"+i+j+", ");
                        for(int k=0; k<machines.size(); k++)
                        {
                            if((i != machines.size()-1) && k != i)
                            {
                                fw.write("y_"+i+j+k+", ");
                            }
                            else if(i == machines.size()-1 && k != i)
                            {
                                if(i == machines.size()-1 && k == machines.size()-2 && j == tasks.size()-1)
                                {
                                    fw.write("y_"+i+j+k+";");
                                }
                                else
                                    fw.write("y_"+i+j+k+", ");
                            }
                        }
                    }

                    
                }

            }
            fw.close();

            ts++;
        }
        


    }
}
