package util;

import java.util.ArrayList;

public class CircularArray {
    private ArrayList<Integer> array;
    private int i;
    private int sum;

    public CircularArray(int size) {
        this.array = new ArrayList<>(size);
        for (int j = 0; j < size; j++) {
            this.array.add(0);
        }

        this.i = 0;
        this.sum = 0;
    }

    public void insert(int num) {
        if (this.i >= this.array.size())
            this.i = 0;

        this.sum -= this.array.get(this.i);
        this.array.set(this.i, num);
        this.sum += num;

        this.i++;
    }

    public int getSum() {
        return this.sum;
    }

    public String toString() {
        return "idx: " + this.i + " sum: " + this.sum + " " + this.array.toString();
    }

    public static void main(String[] args) {
        CircularArray a = new CircularArray(10);
        a.insert(1);
        a.insert(2);
        a.insert(3);
        a.insert(4);
        a.insert(5);
        a.insert(6);
        a.insert(7);
        a.insert(8);
        a.insert(9);
        a.insert(10);
        a.insert(11);
        a.insert(12);

        System.out.println(a);
    }
}
