public class MyClass {
    public static void main(String args[]) {
       
        permutation("ABCDEF");
    }
    
public static void permutation(String str) { 
    permutationWithRepeatTimes("", str,str.length(),str.length()-2); 
}

private static void permutationWithRepeat(String prefix, String str,int len) {
  //  int n = str1.length();
    if (len == 0)
    System.out.println(prefix);
    else {
        for (int i = 0; i < str.length(); i++)
            permutationWithRepeat(prefix + str.charAt(i),str,len-1);
    }
}
private static void permutationWithoutRepeat(String prefix, String str) {
   int n = str.length();
    if (n == 0) System.out.println(prefix);
    else {
        for (int i = 0; i < n; i++)
            permutationWithoutRepeat(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n));
    }
}
private static void permutationWithRepeatTimes(String prefix, String str,int len,int num) {
    if (len == num)
    System.out.println(prefix);
    else {
        for (int i = 0; i < str.length(); i++)
            permutationWithRepeatTimes(prefix + str.charAt(i),str,len-1,num);
    }
}

}
