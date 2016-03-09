package com.nk.sort;

public class SortTest {

	public static void main(String[] args) {
		int a[] = {3,1,5,7,2,4,9,6};  
	    InsertSort(a,8);  
	    print(a,8,8); 
	}

	static void InsertSort(int a[], int n)  
	{  
	    for(int i= 1; i<n; i++){  
	        if(a[i] < a[i-1]){               //若第i个元素大于i-1元素，直接插入。小于的话，移动有序表后插入  
	            int j= i-1;   
	            int x = a[i];        //复制为哨兵，即存储待排序元素  
	            a[i] = a[i-1];           //先后移一个元素  
	            while(j>=0 && x < a[j]){  //查找在有序表的插入位置  
	                a[j+1] = a[j];  
	                j--;         //元素后移  
	            }  
	            a[j+1] = x;      //插入到正确位置  
	        }  
	        print(a,n,i);           //打印每趟排序的结果  
	    }  
	      
	}

	private static void print(int[] a, int n, int i) {
		for(int j = 0; j < i ; j++){
			System.out.print(a[j]+" ");
		}
		System.out.println();
	}  
	  
}
