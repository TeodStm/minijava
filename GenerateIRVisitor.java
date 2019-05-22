import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;
import java.io.*;

public class GenerateIRVisitor extends GJDepthFirst<String,ArgumentsForVisit>{

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
		//define i32 @main() {
		try{
			String class_name = n.f1.f0.accept(this, ar); //class Identifier

			ar.emit("define 132 @main (){\n");
			
			ar.curr_class = class_name;
			ar.curr_method = "main";

			//n.f14.accept(this, ar); //VarDeclaration

			ar.emit("}\n");
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
			//%num_aux = alloca i32

			String type = n.f0.accept(this, ar); //Type
			String ident = n.f1.f0.accept(this, ar); //Identifier
			String ir_type = null;

			if ( type.equals("int") ) ir_type = "i32";              //typos int
			else if ( type.equals("boolean") ) ir_type = "i1";      //typos boolean
			else if ( type.equals("int[]") ) ir_type = "i32*";      //typos int[]
			else ir_type = "i8*";                                   //typos deikth

			ar.emit("\t%"+ident+" = alloca "+ir_type+"\n");

		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ClassExtendsDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			//define i32 @TV.Start(i8* %this) {
			String class_name = n.f1.f0.accept(this, ar); //class name (Identifier)
			//String parent_class_name = n.f3.f0.accept(this, ar); //super class name (Identifier)
			
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
			String class_name = n.f1.f0.accept(this, ar); //Identifier

			ar.curr_class = class_name;
			ar.curr_method = " ";

			//n.f3.accept(this, ar); //varDeclaration
			n.f4.accept(this, ar); //MethodDeclaration

		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(MethodDeclaration n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			//define returnType @ClassName.MethodName(i8* %this) {

			String ret_type = n.f1.accept(this, ar); //return Type
			String method_name = n.f2.f0.accept(this, ar); //method name (Identifier)

			ar.curr_method = method_name;
			ar.register_count = 0;

			//typos epistrofhs
			String ir_return_type = null;
			if ( ret_type.equals("int") ) ir_return_type = "i32";           //typos int
			else if ( ret_type.equals("Boolean") ) ir_return_type = "i1";   //typos boolean
			else if ( ret_type.equals("int[]") ) ir_return_type = "i32*";   //typos int[]
			else ir_return_type = "i8*";                                    //typos deikth

			ar.emit("define "+ir_return_type+" @"+ar.curr_class+"."+method_name+"(i8* %this");

			//arguments
			String arguments = "";

			for(String arg : ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.keySet() ){ //gia kathe argument ths methodou
				if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("int") ) arguments = arguments + ", i32 %."+arg; //tupos int
				else if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("boolean") ) arguments = arguments + ", i1 %."+arg; //tupos boolean
				else if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(arg).equals("int[]") ) arguments = arguments + ", i32* %."+arg;
				else arguments = arguments + ", i8* %."+arg;

			}

			ar.emit(arguments+") {\n");

			n.f4.accept(this, ar); //FormalParameterList (arguments)
			n.f7.accept(this, ar); //VarDeclaration
			n.f8.accept(this, ar); //Statement


			String exp = n.f10.accept(this, ar); //Expression (gia return)

			ar.emit("\t%_"+ar.register_count.toString()+" = load "+ir_return_type+", "+exp+"\n\n"+"\tret "+"%_"+ar.register_count.toString()+"\n");
			ar.register_count++;

			ar.emit("}\n\n");			
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
			//px
			//%v = alloca i8*
			//store i8* %.v, i8** %v

			String type = n.f0.accept(this, ar); //Type
			String ident = n.f1.f0.accept(this, ar); //Identifier

			String ir_type = null;
			if ( type.equals("int") ) ir_type = "i32";          //typos int
			else if( type.equals("boolean") ) ir_type = "i1";   //typos boolean
			else if( type.equals("int[]") ) ir_type = "i32*";   //typos int[]
			else ir_type = "i8*";                               //typos deikth

			ar.emit("\t%"+ident+" = alloca "+ir_type+"\n");
			ar.emit("\tstore "+ir_type+" %."+ident+", "+ir_type+"* %"+ident+"\n\n");
			//ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.put(ident, type);
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

	/****     STATEMENTS     *****/
	public String visit(Statement n, ArgumentsForVisit ar) throws TypeCheckException {
		//Block, AssignmentStatement, ArrayAssignmentStatement, IfStatement, PrintStatement
		try{
			String result = n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(AssignmentStatement n, ArgumentsForVisit ar) throws TypeCheckException {
		//Identifier = Expression
		try{
			String ident = n.f0.f0.accept(this, ar); //Identifier
			String exp = n.f2.accept(this, ar);      //Expression
			String ir_type = null;

			if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.containsKey(ident) ){ //einai topikh metavlhth ths methodou
				if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("int") ) ir_type = "i32";            //typos int
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("boolean") ) ir_type = "i1";    //typos boolean
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("int[]") ) ir_type = "i32*";    //typos int []
				else ir_type = "i8*";                                                                                                            //typos deikth
			}
			else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.containsKey(ident) ){  //einai kapoio orisma ths methodou
				if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("int") ) ir_type = "i32";            //typos int
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("boolean") ) ir_type = "i1";    //typos boolean
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("int[]") ) ir_type = "i32*";    //typos int []
				else ir_type = "i8*";
			}
			else{ //einai metavlhth-pedio ths klashs

			}

			ar.emit("\tstore "+exp+" ,"+ir_type+"* "+ident+"\n\n");
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	/*****   EXPRESSIONS   *****/
	public String visit(Expression n, ArgumentsForVisit ar) throws TypeCheckException {
		//Expression : AndExpression,CompareExpression,PlusExpression,MinusExpression,TimesExpression
		//ArrayLookup,ArrayLength,MessageSend,Clause
		try{
			return n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(AndExpression n, ArgumentsForVisit ar) throws TypeCheckException{
		//Clause && Clause
		try{

		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(CompareExpression n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression < PrimaryExpression
		try{
			String prim_exp1 = n.f0.accept(this, ar);   //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar);   //PrimaryExpression

		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(PlusExpression n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression + PrimaryExpression
		//%_13 = sub i32 %_12, 1
		try{
			String prim_exp1 = n.f0.accept(this, ar);   //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar);   //PrimaryExpression
			String ir_type = null;
			String register1 = null;
			String register2 = null;
			String result_register = null;

			//PrimaryExpression_1
			if ( prim_exp1.startsWith("i32*") ){  //typos int
				ir_type = "i32";
				ar.emit("\t%_"+ar.register_count.toString()+" = load "+ir_type+", "+prim_exp1+"\n");
				register1 = "%_"+ar.register_count.toString();
				ar.register_count++;
			}
			else{    //IntegerLiteral
				ir_type = "i32";
				register1 = prim_exp1;
			}

			//PrimaryExpression_2
			if ( prim_exp2.startsWith("i32*") ){   //typos int[]
				ir_type = "i32";
				ar.emit("\t%_"+ar.register_count.toString()+" = load "+ir_type+", "+prim_exp2+"\n");
				register2 = "%_"+ar.register_count.toString();
				ar.register_count++;
			}
			else{        //IntegerLiteral
				ir_type = "i32";
				register2 = prim_exp2;
			}

			result_register = ar.register_count.toString();
			ar.emit("\t%_"+result_register+" = add "+ir_type+" "+register1+", "+register2+"\n\n" );
			ar.register_count++;

			return "%_"+result_register;
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(MinusExpression n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression - PrimaryExpression
		try{
			String prim_exp1 = n.f0.accept(this, ar);   //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar);   //PrimaryExpression
			String ir_type = null;
			String register1 = null;
			String register2 = null;
			String result_register = null;

			//PrimaryExpression_1
			if ( prim_exp1.startsWith("i32*") ){  //typos int
				ir_type = "i32";
				ar.emit("\t%_"+ar.register_count.toString()+" = load "+ir_type+", "+prim_exp1+"\n");
				register1 = "%_"+ar.register_count.toString();
				ar.register_count++;
			}
			else{    //IntegerLiteral
				ir_type = "i32";
				register1 = prim_exp1;
			}

			//PrimaryExpression_2
			if ( prim_exp2.startsWith("i32*") ){   //typos int[]
				ir_type = "i32";
				ar.emit("\t%_"+ar.register_count.toString()+" = load "+ir_type+", "+prim_exp2+"\n");
				register2 = "%_"+ar.register_count.toString();
				ar.register_count++;
			}
			else{        //IntegerLiteral
				ir_type = "i32";
				register2 = prim_exp2;
			}

			result_register = ar.register_count.toString();
			ar.emit("\t%_"+result_register+" = add "+ir_type+" "+register1+", "+register2+"\n\n" );
			ar.register_count++;

			return "%_"+result_register;
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(TimesExpression n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression - PrimaryExpression
		try{
			String prim_exp1 = n.f0.accept(this, ar);   //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar);   //PrimaryExpression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ArrayLookup n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression [ PrimaryExpression ]
		try{
			String prim_exp1 = n.f0.accept(this, ar);   //PrimaryExpression
			String prim_exp2 = n.f2.accept(this, ar);   //PrimaryExpression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(ArrayLength n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression.length
		try{
			String prim_exp = n.f0.accept(this, ar);   //PrimaryExpression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(MessageSend n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression.Identifier( ExpressionList )
		try{
			String prim_exp = n.f0.accept(this, ar);   //PrimaryExpression
			String ident = n.f2.accept(this, ar);      //Identifier
			String exp_list = n.f4.accept(this, ar);   //ExpressionList
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return null;
	}

	public String visit(Clause n, ArgumentsForVisit ar) throws TypeCheckException{
		//PrimaryExpression | NotExpression
		try{
			return n.f0.accept(this, ar); // NotExpression or PrimaryExpression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	/***** CLAUSES ******/

	public String visit(PrimaryExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		//  IntegerLiteral, TrueLiteral, FalseLiteral, Identifier, ThisExpression,
		//	ArrayAllocationExpression,   AllocationExpression,   BracketExpression
		try{
			return n.f0.accept(this, ar);
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		//return null;
	}

	public String visit(NotExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// ! Clause
		try{
			n.f0.accept(this, ar);
			return null;
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	/**** TERMINALS ****/
	public String visit(IntegerLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			String int_lit = n.f0.accept(this, ar); //NodeToken
			String result_register = ar.register_count.toString();

			//ar.emit("\tstore i32 "+int_lit+", i32* %_"+result_register+"\n");
			//ar.register_count++;
			return int_lit;
			//return "i32* %_"+result_register;
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(TrueLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "i1 1";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(FalseLiteral n, ArgumentsForVisit ar) throws TypeCheckException {
		try{
			return "i1 0";
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	public String visit(ThisExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		//return "this";
		return ar.curr_class;
	}

	public String visit(ArrayAllocationExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// new int [ exp ]
		//thelei calloc
		return "int[]";
		
	}

	public String visit(AllocationExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// new Identifier
		//thelei calloc
		String result = null;
		try{
			result = n.f1.accept(this, ar); //Identifier
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return result;
	}

	public String visit(BracketExpression n, ArgumentsForVisit ar) throws TypeCheckException {
		// ( exp )
		String exp = null;
		try{
			exp = n.f1.accept(this, ar); //Expression
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
		return exp;
	}
	/******************/

	public String visit(Identifier n, ArgumentsForVisit ar) throws TypeCheckException {
		//thelei diaxwrismo gia main class
		try{
			String ident = n.f0.accept(this, ar);  //NodeToken
			String ir_type = null;

			if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.containsKey(ident) ){  //einai topikh metavlhth ths methodou
				if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("int") ) ir_type = "i32* ";          //typos int
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("boolean") ) ir_type = "i1* ";  //typos boolean
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).local_vars_map.get(ident).equals("int[]") ) ir_type = "i32** ";  //typos int[]
				else ir_type = "i8** ";                                                                                                        //typos deikth
			}
			else if( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.containsKey(ident) ){  //einai kapoio orisma ths methodou
				if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("int") ) ir_type = "i32* ";          //typos int
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("boolean") ) ir_type = "i1* ";  //typos boolean
				else if ( ar.m.get(ar.curr_class).methodMap.get(ar.curr_method).args_map.get(ident).equals("int[]") ) ir_type = "i32** ";  //typos int[]
				else ir_type = "i8** ";                                                                                                    //typos deikth
			}
			else{  //einai metavlhth-pedio ths klashs

			}
			return ir_type+ident;
		}
		catch(Exception e){
			throw new TypeCheckException();
		}
	}

	/**** TYPES ****/
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