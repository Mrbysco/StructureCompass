package com.mrbysco.structurecompass.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompassScreen extends Screen {
    private static final int SCROLLHEIGHT = 600;
    private static final int SCROLLWIDTH = 200;
    private final PlayerEntity editingPlayer;
    private final ItemStack compass;
    private final Hand hand;
    
    // Scroll settings
    private static final int PAGESIZE = 6;
    private static final int GUILEFT = 16;
    private static final int GUITOP = 16;
    private int listEnd = 0;
    private int sliderIndex = 0;
    
    //full list before filtering
    private List<CompassDataRow> allRows = new ArrayList<>();

    private String defaultInputFieldText = "";
    protected TextFieldWidget inputField;

    public CompassScreen(PlayerEntity player, ItemStack compassIn, Hand handIn, List<String> availableStructureList) {
        super(new TranslationTextComponent(Reference.MOD_PREFIX + "compass.screen"));
        this.editingPlayer = player;
        this.compass = compassIn;
        this.hand = handIn;
        
        int index = 0;
        for(ResourceLocation id : StructureUtil.getAvailableStructureList()) {
          if(id != null && doesPlayerGamestageAllowStruct(id)) { 
            allRows.add(new CompassDataRow(id, GUILEFT, 0, index));
            index++;
          }
        }
        listEnd = StructureUtil.getAvailableStructureList().size();
        
        
        if(compassIn.hasTag() && compassIn.getTag().contains(Reference.structure_tag)) {
            this.defaultInputFieldText = compassIn.getTag().getString(Reference.structure_tag);
        }
    }

    public static boolean doesPlayerGamestageAllowStruct(ResourceLocation id) {
      // IDK how to do, so say true for now
      //use this.editingPlayer 
      return true;
    }

    @Override
    protected void init() {
        this.inputField = new TextFieldWidget(this.font, ((this.width - 192) / 2) + 4, (this.height / 2) - 4, 192, 12, I18n.format("structurecompass.chat.editBox")) {
            protected String getNarrationMessage() {
                return super.getNarrationMessage(); //+ CompassScreen.this.commandSuggestionHelper.func_228129_c_();
            }
        };
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setText(this.defaultInputFieldText);  
    } 
    
    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        this.renderBackground();
        this.setFocused((IGuiEventListener)null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.setFocused(this.inputField);
        this.inputField.setFocused2(true);
        this.fill(((this.width - 192) / 2), (this.height / 2) - 8, ((this.width - 192) / 2) + 204, (this.height / 2) + 8, this.minecraft.gameSettings.getChatBackgroundColor(Integer.MIN_VALUE));
        this.inputField.render(mouseX, mouseY, partialTick);

        super.render(mouseX, mouseY, partialTick);
        this.renderRows();

    }

    @Override
    public boolean mouseClicked(double mx, double my, int key) {
 
      System.out.println(mx +"  , "+my);

      for(CompassDataRow row : this.allRows) {
        if(isMouseOver(row, mx, my)) {
          this.clicked(row);
        }
        
      }
     return super.mouseClicked(mx, my, key); 
    }

    private void clicked(CompassDataRow row) {
      //if you need this
      System.out.println(row.id + " clicked on this row");
    }

    private boolean isMouseOver(CompassDataRow row, double mx, double my) {
      return row.x < mx && mx < row.x + row.width
          && row.y < my && my < row.y + + row.height;
    }
    
    @Override
    public boolean mouseScrolled(double mx, double my, double key) {
       
      //TODO: could check mx,my vs  GUILEFT, GUITOP to see if i am in a "scrollable area" rectangle 
      if(key < 0) {
        //System.out.println(key+"scrolled negative");
        sliderIndex--;
        if(sliderIndex < 0) {
          sliderIndex = 0;
        }
      }
      if(key > 0) {
        //System.out.println(key+"scrolled positive");
        sliderIndex++;
        if(sliderIndex >= listEnd-1 ) {
          sliderIndex = listEnd-1;
        }
      }
      return super.mouseScrolled(mx, my, key);
    }
    
    private void renderRows() {
      int drawn = 0;
      for(CompassDataRow row : this.allRows) {

        // distance above, then the height of myself
        row.y = GUITOP + row.height * drawn;
        
        if(row.isVisible(this.sliderIndex, this.inputField.getText())) {
         this.render(row);
         drawn++;
        }
        if(drawn > PAGESIZE) {
          break;//lol yup this is hacky way
        }
      }
  }

    private void render(CompassDataRow row) {
      this.font.drawString(row.display
//          +String.format(" (%d,%d) || wxh = %d x %d", row.x, row.y, row.width, row.height)
          , row.x, row.y, 4209792);
      //draw button, texture, whatever here
    }

}
