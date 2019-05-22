import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.io.*;

public class FillSymTabVisitor extends GJDepthFirst<String,ArgumentsForVisit>{

	public String visit(Goal n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); //MainClass
			n.f1.accept(this, ar); //TypeDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}
	
	public String visit(MainClass n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String class_name = n.f1.accept(this, ar); //class Identifier
			ClassMapNode mp_node = new ClassMapNode();

			mp_node.setType("Main class");
			//mp_node.setParentClass(" ");
			ar.m.put(class_name, mp_node);
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

			if( ar.curr_method.equals("main") || ar.curr_method.equals(" ") ){ //periptwsh pediwn klashs
			//if ( ar.m.get(ar.curr_class).type.equals("Main class") ){
				if( ar.m.get(ar.curr_class).fieldMap.containsKey(ident) ){ //h metavlhth exei hdh oristei
					//System.out.println("error: Variable "+ident+" has already be defined");
					throw new TypeCheckException();
				}
				ar.m.get(ar.curr_class).fieldMap.put(ident, type);
				/*if ( ar.m.get(ar.curr_class).parent_class != null ){
					if ( !ar.compatibleWithParentClass(type, ident, ar.m.get(ar.curr_class).parent_class) ){
						System.out.println("error: Incompatible definition of field '"+type+" "+ident+"' with parent class");
						throw new TypeCheckException();
						//return null;
						//System.exit(0);
					}
				}*/
				
			}
			else{   //periptwsh metavlhtwn mias methodou
				ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.put(ident, type);
			}
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


			if ( ar.m.containsKey(class_name) ){ //Uparxei hdh klash me to idio onoma
				System.out.println("error: Class '"+class_name+"' has already defined");
				throw new TypeCheckException();
			}

			if ( !ar.m.containsKey(parent_class_name) ){ //H gonikh klash den exei oristei
				System.out.println("error: Unknown parent class '"+parent_class_name+"'");
				throw new TypeCheckException();
			}

			ClassMapNode mp_node = new ClassMapNode();

			mp_node.setType("class");
			mp_node.setParentClass(parent_class_name);

			ar.m.put(class_name, mp_node);
			
			ar.curr_class = class_name;
			ar.curr_method = " ";

			n.f5.accept(this, ar); //VarDeclaration
			n.f6.accept(this, ar); //MethodDeclaration
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


			if ( ar.m.containsKey(class_name) ){ //Uparxei hdh klash me to idio onoma
				System.out.println("error: Class '"+class_name+"' has already defined");
				throw new TypeCheckException();
			}

			ClassMapNode mp_node = new ClassMapNode();

			mp_node.setType("class");

			ar.m.put(class_name, mp_node);
			ar.curr_class = class_name;
			ar.curr_method = " ";

			n.f3.accept(this, ar); //varDeclaration
			n.f4.accept(this, ar); //MethodDeclaration
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(MethodDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String ret_type = n.f1.accept(this, ar); //return Type
			String method_name = n.f2.accept(this, ar); //method name (Identifier)

			if ( ar.m.get(ar.curr_class).methodMap.containsKey(method_name) ){ //h sunarthsh einai hdh orismenh
				System.out.println("error: Method '"+method_name+"' is already defined");
				throw new TypeCheckException();
			}

			MethodMapNode mp_node = new MethodMapNode();

			mp_node.setReturnType(ret_type);
			ar.m.get(ar.curr_class).methodMap.put(method_name, mp_node);
			ar.curr_method = method_name;

			n.f4.accept(this, ar); //FormalParameterList (arguments)

			if ( ar.m.get(ar.curr_class).parent_class != null ){ //uparxei gonikh klash
				String parent = ar.m.get(ar.curr_class).parent_class;
				if ( ar.methodDefinedOnParentClass(ar.curr_class, parent, method_name) ){
					System.out.println("error: Method '"+method_name+"' of '"+ar.curr_class+"' has different definition at parent class");
					throw new TypeCheckException();
				}
			}

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
			System.out.println("Exception caught");
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
			System.out.println("Exception caught");
			throw new TypeCheckException();
		}

		return null;
	}

	public String visit(FormalParameterTail n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f0.accept(this, ar); ////FormalParameterTerm
		}
		catch(Exception e){
			System.out.println("Exception caught");
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(FormalParameterTerm n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			n.f1.accept(this, ar); ////FormalParameter
		}
		catch(Exception e){
			System.out.println("Exception caught");
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
			System.out.println("Exception caught");
			throw new TypeCheckException();
		}
	}

	public String visit(Type n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return n.f0.accept(this, ar);
		}
		catch(Exception e){
			System.out.println("Exception caught");
			throw new TypeCheckException();
			//return null;
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
			System.out.println("Exception caught");
			throw new TypeCheckException();
		}
	}

}