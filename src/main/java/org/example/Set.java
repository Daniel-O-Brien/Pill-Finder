package org.example;

public class Set {

    public static int find(int[] array, int id) {
        return array[id]==id ? id : ( array[id]= find(array,array[id]));
    }

    public static void union(int[] a, int p, int q) {
        a[find(a,q)]=find(a,p);
    }

}
