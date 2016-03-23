package com.pheu.thrift.example;

public class LamdaMain<A, B, C, D> {

	private interface TriFunction<A, B, C, D> {
		A apply(B b, C c, D d);
	}
	
	public String inBCD(String b, String c, String d) {
		String result = b + c + d;
		return result;
	}
	
	public A get(TriFunction<A, B, C, D> func, B b, C c, D d) {
		return func.apply(b, c, d);
	}
	
	public static void main(String[] args) {
		
		LamdaMain<String, String, String, String> main = new LamdaMain<>();
		System.out.println(main.get(main::inBCD, "1", "2", "3"));
	}
	
}
