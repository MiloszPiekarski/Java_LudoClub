package com.example.gry.go.gui.views;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.StateOfIntersection;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class IntersectionView extends Button {
    private final int row;
    private final int col;

    public IntersectionView(int row, int col) {
        this.row = row;
        this.col = col;

        setMinSize(40, 40);
        setMaxSize(40, 40);
        setStyle("-fx-background-radius: 50%;");
        updateState(null);
    }

    public void updateState(Board board) {
        if (board == null) {
            setStyle("-fx-background-color: #e6b800;");
            return;
        }

        StateOfIntersection state = board.getIntersection(row, col).getIntersectionState();
        switch (state) {
            case BLACK -> setStyle("-fx-background-color: black; -fx-background-radius: 50%;");
            case WHITE -> setStyle("-fx-background-color: white; -fx-background-radius: 50%;");
            default -> setStyle("-fx-background-color: #e6b800;");
        }
    }
}
