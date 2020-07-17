package com.mrbysco.structurecompass.client.screens;

import net.minecraft.util.ResourceLocation;

public class CompassDataRow {
  public ResourceLocation id;
  public String display;
  public int x;
  public int y;
  public int width = 200;
  public int height = 18;
  public int index;

  public CompassDataRow(ResourceLocation id, int x, int y, int index) {
    this.id = id;
    this.display = id.toString();
    this.x = x;
    this.y = y;
    this.index = index;
  }


  public boolean isVisible(int sliderIndex, String text) {
    //sliderIndex starts at zero, so start there
    if(index < sliderIndex) {
      return false;
    }
    //gamestage could be checked here
    return compareSearch(text)
        && CompassScreen.doesPlayerGamestageAllowStruct(id);
  }

  // could be better with regex. compare overlap of my display vs input (from text or whatever)
  public boolean compareSearch(String input) {
    if(input==null || input.isEmpty()) {return true;}
    return input.toLowerCase().contains(display) || 
        display.toLowerCase().contains(input);
  }
  
  public boolean isInside(int mouseX, int mouseY) {
    int loffset = 36;
    int roffset = 36;
    return loffset + x < mouseX && mouseX < x + width - roffset &&
        y < mouseY && mouseY < y + height;
  }


 

}
