package io.kevin;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TopoConf {
    ArrayList<String> targets;
    int my_index;

    TopoConf(String file_name, int idx) throws Exception {
        targets = new ArrayList<>();
        File file_obj = new File(file_name);
        Scanner reader = new Scanner(file_obj);
        while (reader.hasNextLine()) {
            targets.add(reader.nextLine());
        }
        my_index = idx;
    }

    public int port_from_target(String target) {
        String[] words = target.split(":");
        return Integer.parseInt(words[1]);
    }

    public ArrayList<String> get_targets() {
        return targets;
    }

    public int get_my_idx() {
        return my_index;
    }
}