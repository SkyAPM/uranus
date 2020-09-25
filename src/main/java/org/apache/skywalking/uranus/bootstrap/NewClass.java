package org.apache.skywalking.uranus.bootstrap;

public class NewClass extends BaseBootstrap {

    public static void main(String[] args) {
        OldClass.parseOptions(args, false);
    }
}
