import java.io.*;
import java.util.*;

class ClassMapNode{
    String type;                    //class
    LinkedHashMap<String,String> fieldMap;
    LinkedHashMap<String,MethodMapNode> methodMap;
    String parent_class;
    LinkedHashMap<String,Integer> offsetFieldMap;
    LinkedHashMap<String,Integer> offsetMethodMap;

    public ClassMapNode(){
        this.type = null;
        this.fieldMap = new LinkedHashMap<String,String>();
        this.methodMap = new LinkedHashMap<String,MethodMapNode>();
        this.offsetFieldMap = new LinkedHashMap<String,Integer>();
        this.offsetMethodMap = new LinkedHashMap<String,Integer>();
        this.parent_class = null;
    }

    public void printNode(){
        System.out.println("    >Type< : "+this.type);
        System.out.println("    >Parent class< : "+this.parent_class);

        System.out.println("    >Fields< :");
        for(String i : this.fieldMap.keySet()){
            System.out.print("\t"+i+" : ");
            System.out.println(this.fieldMap.get(i));
        }

        System.out.println("    >Methods< :");
        for(String i : this.methodMap.keySet()){
            System.out.print("\t\t"+i+" ");
            System.out.println(this.methodMap.get(i).return_type);
            System.out.println("\t\t\t>Arguments< :");
            methodArgsPrint(this.methodMap.get(i));
            System.out.println("\t\t\t>Local Variables< :");
            methodVarsPrint(this.methodMap.get(i));
        }


    }

    public void methodArgsPrint(MethodMapNode node){
        for(String i : node.args_map.keySet()){
            System.out.print("\t\t\t\t"+i+" : ");
            System.out.println(node.args_map.get(i));
        }

    }
    public void methodVarsPrint(MethodMapNode node){
        for(String i : node.local_vars_map.keySet()){
            System.out.print("\t\t\t\t"+i+" : ");
            System.out.println(node.local_vars_map.get(i));
        }

    }

    public void setType(String type){
        this.type = type;   
    }
    public void setParentClass(String parent_class){
        this.parent_class = parent_class;
    }
}
