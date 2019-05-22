import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.io.*;

public class GenerateVTablesVisitor extends GJDepthFirst<String,ArgumentsForVisit>{

	public String visit(Goal n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); //MainClass
			n.f1.accept(this, ar); //TypeDeclaration
			ar.default_emit();
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}
	
	public String visit(MainClass n, ArgumentsForVisit ar) throws TypeCheckException {
		//@.Identifier_vtable = global [0 x i8*] []
		try{
			String class_name = n.f1.accept(this, ar); //class Identifier

			ar.emit("@."+class_name+"_vtable = global [0 x i8*] []\n");
			//ClassMapNode mp_node = new ClassMapNode();

			//mp_node.setType("Main class");
			//mp_node.setParentClass(" ");
			//ar.m.put(class_name, mp_node);
			ar.curr_class = class_name;
			ar.curr_method = "main";
			n.f14.accept(this, ar); //VarDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(TypeDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); //ClassDeclaration or ClassExtendsDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(VarDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {

		try{
			String type = n.f0.accept(this, ar); //Type
			String ident = n.f1.accept(this, ar); //Identifier
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ClassExtendsDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String class_name = n.f1.accept(this, ar); //class name (Identifier)
			String parent_class_name = n.f3.accept(this, ar); //super class name (Identifier)

			Integer num_of_methods = ar.m.get(class_name).methodMap.size();
			String num_of_methods_str = num_of_methods.toString();
			
			ar.curr_class = class_name;
			ar.curr_method = " ";

			ar.emit("@."+class_name+"_vtable = global ["+num_of_methods_str+" x i8*] [");

			int count = 0;
			for(String methods : ar.m.get(class_name).methodMap.keySet() ){ //gia kathe methodo ths klashs
				//arguments
				String arguments = "";
				String return_type = "";
				for(String arg : ar.m.get(class_name).methodMap.get(methods).args_map.keySet() ){
					if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("int") ) arguments = arguments + ", i32"; //tupos int
					else if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("boolean") ) arguments = arguments + ", i1"; //tupos boolean
					else if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("int[]") ) arguments = arguments + ", i32*";
					else arguments = arguments + ", i8*";

				}

				//tupos epistrofhs
				String ret_type = ar.m.get(class_name).methodMap.get(methods).return_type;
				if( ret_type.equals("int") ) return_type = "i32";
				else if( ret_type.equals("boolean") ) return_type = "i8";
				else if( ret_type.equals("int[]") ) return_type = "i32*";
				else return_type = "i8*";

				count++;
				if( count == ar.m.get(class_name).methodMap.size() ) ar.emit("i8* bitcast ("+return_type+" (i8*"+arguments+")* @"+class_name+"."+methods+" to i8*) "); //teleutaia sunarthsh (den vazoume to komma)
				else ar.emit("i8* bitcast ("+return_type+" (i8*"+arguments+")* @"+class_name+"."+methods+" to i8*), ");
			}

			//n.f5.accept(this, ar); //VarDeclaration
			//n.f6.accept(this, ar); //MethodDeclaration

			ar.emit("]\n");
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ClassDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			ar.curr_class = " ";
			ar.curr_method = " ";
			String class_name = n.f1.accept(this, ar); //Identifier

			Integer num_of_methods = ar.m.get(class_name).methodMap.size();
			String num_of_methods_str = num_of_methods.toString();
			
			ar.emit("@."+class_name+"_vtable = global ["+num_of_methods_str+" x i8*] [");

			ar.curr_class = class_name;
			ar.curr_method = " ";

			int count = 0;
			for(String methods : ar.m.get(class_name).methodMap.keySet() ){ //gia kathe methodo ths klashs
				//arguments
				String arguments = "";
				String return_type = "";
				for(String arg : ar.m.get(class_name).methodMap.get(methods).args_map.keySet() ){
					if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("int") ) arguments = arguments + ", i32"; //tupos int
					else if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("boolean") ) arguments = arguments + ", i1"; //tupos boolean
					else if( ar.m.get(class_name).methodMap.get(methods).args_map.get(arg).equals("int[]") ) arguments = arguments + ", i32*";
					else arguments = arguments + ", i8*";

				}

				//tupos epistrofhs
				String ret_type = ar.m.get(class_name).methodMap.get(methods).return_type;
				if( ret_type.equals("int") ) return_type = "i32";
				else if( ret_type.equals("boolean") ) return_type = "i8";
				else if( ret_type.equals("int[]") ) return_type = "i32*";
				else return_type = "i8*";

				count++;
				if( count == ar.m.get(class_name).methodMap.size() ) ar.emit("i8* bitcast ("+return_type+" (i8*"+arguments+")* @"+class_name+"."+methods+" to i8*) "); //teleutaia sunarthsh (den vazoume to komma)
				else ar.emit("i8* bitcast ("+return_type+" (i8*"+arguments+")* @"+class_name+"."+methods+" to i8*), ");
			}


			//n.f3.accept(this, ar); //varDeclaration
			//n.f4.accept(this, ar); //MethodDeclaration

			ar.emit("]\n");
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(MethodDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			//i8* bitcast (i32 (i8*)* @BT.Start to i8*)

			String ret_type = n.f1.accept(this, ar); //return Type
			String method_name = n.f2.accept(this, ar); //method name (Identifier)

			ar.curr_method = method_name;

			n.f4.accept(this, ar); //FormalParameterList (arguments)

			//arguments
			String arguments = null;
			String return_type = null;
			for(String arg : ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.keySet() ){

				if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("int") ) arguments = arguments + ", i32"; //tupos int
				else if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("boolean") ) arguments = arguments + ", i1"; //tupos boolean
				else if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("int[]") ) arguments = arguments + ", i32*";
				else arguments = arguments + ", i8*";

			}

			//tupos epistrofhs
			if( ret_type.equals("int") ) return_type = "i32";
			else if( ret_type.equals("boolean") ) return_type = "i8";
			else if( ret_type.equals("int[]") ) return_type = "i32*";
			else return_type = "i8*";

			ar.emit("i8* bitcast ("+return_type+" (i8*"+arguments+")* @"+ar.curr_class+"."+ar.curr_method+" to i8*) ");
			//ar.emit("i8* bitcast (i32 (i8*)* @BT.Start to i8*)");

			n.f7.accept(this, ar); //VarDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(FormalParameterList n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); //FormalParameter
			n.f1.accept(this, ar); //FormalParameterTail
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(FormalParameter n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String type = n.f0.accept(this, ar); //type
			String ident = n.f1.accept(this, ar); //identifier

			ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.put(ident, type);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}

		return null;
	}

	public String visit(FormalParameterTail n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); ////FormalParameterTerm
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(FormalParameterTerm n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f1.accept(this, ar); ////FormalParameter
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	/******************/

	public String visit(Identifier n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(Type n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(ArrayType n, ArgumentsForVisit ar) throws TypeCheckException {
		return "int[]";
	}

	public String visit(BooleanType n, ArgumentsForVisit ar) throws TypeCheckException {
		return "boolean";
	}

	public String visit(IntegerType n, ArgumentsForVisit ar) throws TypeCheckException {
		return "int";
	}

	public String visit(NodeToken n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return n.toString();
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

}