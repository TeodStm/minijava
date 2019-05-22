import java.io.*;
import java.util.*;

class MethodMapNode{
    String return_type;
    LinkedHashMap<String,String> args_map;
    LinkedHashMap<String,String> local_vars_map;

    public MethodMapNode(){
        this.return_type = null;
        this.args_map = new LinkedHashMap<String,String>();
        this.local_vars_map = new LinkedHashMap<String,String>();
    }

    public void printNode(){
        System.out.println("    Return type : "+this.return_type);
    }


    public void setReturnType(String ret_type){
        this.return_type = ret_type;
    }

}
