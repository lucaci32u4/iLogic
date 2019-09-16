package com.lucaci32u4.circuit;

import com.lucaci32u4.util.Rotation;



public class CircuitComponent {

    private int posX = 0, posY = 0;
    private int width = 0, height = 0;
    private CircuitPin[] pins;
    private Rotation rot = Rotation.R0;

    protected void initComponent(CircuitPin[] pinsInfo, int width, int height) {
        pins = pinsInfo.clone();
        this.width = width;
        this.height = height;
    }

    public final int getPositionX() {
        return posX;
    }

    public final int getPositionY() {
        return posY;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final CircuitPin[] getPins() {
        return pins;
    }

    void setPosition(int x, int y) {
        posX = x;
        posY = y;
        updatePinPositions();
    }

    void setRotation(Rotation rot) {
        this.rot = rot;
        updatePinPositions();
    }

    private void updatePinPositions() {
        int rotCount = rot.getAngle() / 90;
        for (CircuitPin pin : pins) {
            pin.worldX = pin.x - width / 2;
            pin.worldY = pin.y - height / 2;
            for (int i = 0; i < rotCount; i++) {
                int aux = pin.worldX;
                pin.worldX = pin.worldY;
                pin.worldY = -aux;
            }
            if (rotCount % 2 == 1) {
                int aux = width;
                width = height;
                height = aux;
            }
            pin.worldX += posX + width / 2;
            pin.worldY += posY + height / 2;
        }
    }
}
