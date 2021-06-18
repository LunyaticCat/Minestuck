package com.mraof.minestuck.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class CassettePlayerScreen extends Screen
{
	private static final ResourceLocation guiBackground = new ResourceLocation("minestuck", "textures/gui/transportalizer.png");

	private static final int guiWidth = 126;
	private static final int guiHeight = 98;
	
	private TextFieldWidget destinationTextField;
	
	private final PlayerEntity player;
	private final Hand hand;
	
	
	CassettePlayerScreen(PlayerEntity playerIn, Hand handIn)
	{
		super(new StringTextComponent("CassettePlayer"));
		
		this.hand = handIn;
		player = playerIn;
	}

	@Override
	public void init()
	{
		int yOffset = (this.height / 2) - (guiHeight / 2);
		this.destinationTextField = new TextFieldWidget(this.font, this.width / 2 - 20, yOffset + 25, 40, 20, "Transportalizer destination code");	//TODO Use translation instead, and maybe look at other text fields for what the text should be
		this.destinationTextField.setMaxStringLength(4);
		this.destinationTextField.setFocused2(true);
		setFocusedDefault(destinationTextField);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		this.minecraft.getTextureManager().bindTexture(guiBackground);
		int yOffset = (this.height / 2) - (guiHeight / 2);
		this.blit((this.width / 2) - (guiWidth / 2), yOffset, 0, 0, guiWidth, guiHeight);
		super.render(mouseX, mouseY, partialTicks);
	}

}

