import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main (String [] args){
        //if(args.length != 1){
        if(args.length < 1){
            System.err.println("Usage: java Main <inputFile0> <inputFile1> ...");
            System.exit(1);
        }
        FileInputStream fis = null;

        for(int a = 0; a < args.length; a++){
            System.out.println("\nCompiling '"+args[a]+"' ...");
            try{
            
                ArgumentsForVisit visit_args = new ArgumentsForVisit();
                String file_name = args[a]; //onoma arxeiou eisodou
                String[] tokens = file_name.split("[.]");
                String generated_file = tokens[0] + ".ll";

                visit_args.initWriter(generated_file);

                fis = new FileInputStream(args[a]);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                System.err.println("Program parsed successfully.");
                FillSymTabVisitor fillST = new FillSymTabVisitor();
                
                root.accept(fillST, visit_args); // fill Symbol Table
                
                visit_args.clearStrings();
                //TypeCheckVisitor tcv = new TypeCheckVisitor();
                //root.accept(tcv, visit_args); //Type checking

                /***** generate IR visitors *****/
                //1st Visitor
                GenerateVTablesVisitor gen_vtable_vis = new GenerateVTablesVisitor();
                root.accept(gen_vtable_vis, visit_args); //Generate IR

                //2nd Visitor
                GenerateIRVisitor gen_ir_vis = new GenerateIRVisitor();
                root.accept(gen_ir_vis, visit_args); //Generate IR

                //System.out.println("Type Checking done!\n");
                visit_args.outputOffsets();
                visit_args.closeWriter();
            }
            catch(Exception ex){
                    System.out.println(ex.getMessage());
                    //System.out.println("Type Check Failed");
                    continue;
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }


    }
}


