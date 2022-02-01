package kup.get.config;

import javafx.scene.Node;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class InputControl {
    private List<Node> field;
    private Node error;

    public  InputControl (Node ... nodes){
        field = Arrays.asList(nodes);
    }
}
