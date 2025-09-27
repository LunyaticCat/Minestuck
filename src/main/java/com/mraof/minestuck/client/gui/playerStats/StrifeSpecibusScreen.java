package com.mraof.minestuck.client.gui.playerStats;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mraof.minestuck.client.gui.MSScreenFactories;
import com.mraof.minestuck.client.gui.captchalouge.SylladexScreen;
import com.mraof.minestuck.inventory.StrifeSpecibusMenu;
import com.mraof.minestuck.inventory.captchalogue.Modus;
import com.mraof.minestuck.inventory.captchalogue.ModusType;
import com.mraof.minestuck.inventory.captchalogue.ModusTypes;
import com.mraof.minestuck.item.CaptchaCardItem;
import com.mraof.minestuck.network.CaptchaDeckPackets;
import com.mraof.minestuck.player.ClientPlayerData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;

public class StrifeSpecibusScreen extends PlayerStatsContainerScreen<StrifeSpecibusMenu>
{
	public static final String TITLE = "minestuck.strife_specibus";
	public static final String USE_ITEM = "minestuck.captcha_deck.use_item";
	public static final String KIND_ABSTRATUS = "minestuck.kind_abstratus";
	
	private static final ResourceLocation guiStrifeSelector = ResourceLocation.fromNamespaceAndPath("minestuck", "textures/gui/strife_selector.png");
	
	public StrifeSpecibusScreen(int windowId, Inventory playerInventory)
	{
		super(new StrifeSpecibusMenu(windowId, playerInventory), playerInventory, Component.translatable(TITLE));
		guiWidth = 178;
		guiHeight=	 255;
	}
	
	@Override
	public void init()
	{
		super.init();
	}
	
	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY)
	{
		
		drawTabs(guiGraphics);
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		guiGraphics.blit(guiStrifeSelector, xOffset, yOffset, 0, 0, guiWidth, guiHeight);
		
		drawActiveTabAndIcons(guiGraphics);
		
	}
	
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		String message = getTitle().getString();
		guiGraphics.drawString(font, message, (this.width / 2F) - font.width(message) / 2F - leftPos - 15, yOffset + 12 - topPos, 0xafafaf, false);
		
	}
	
	private void use() {
		ItemStack stack = menu.getMenuItem();
		if(!stack.isEmpty())
		{
			if(!(stack.getItem() instanceof CaptchaCardItem))
			{
				ModusType<?> type = ModusTypes.getTypeFromItem(stack.getItem());
				Modus newModus = type.createClientSide();
				Modus modus = ClientPlayerData.getModus();
				if(newModus != null && modus != null && newModus.getClass() != modus.getClass() && !newModus.canSwitchFrom(modus))
				{
					minecraft.screen = new ConfirmScreen(this::onConfirm, Component.translatable(SylladexScreen.EMPTY_SYLLADEX_1), Component.translatable(SylladexScreen.EMPTY_SYLLADEX_2))
					{
						@Override
						public void removed()
						{
							minecraft.screen = StrifeSpecibusScreen.this;
							minecraft.player.closeContainer();
						}
					};
					minecraft.screen.init(minecraft, width, height);
					return;
				}
			}
			PacketDistributor.sendToServer(new CaptchaDeckPackets.TriggerModusButton());
		}
	}
	
	private void sylladex()
	{
		if( ClientPlayerData.getModus() != null)
		{
			minecraft.player.connection.send(new ServerboundContainerClosePacket(minecraft.player.containerMenu.containerId));
			MSScreenFactories.displaySylladexScreen(ClientPlayerData.getModus());
			minecraft.player.containerMenu = minecraft.player.inventoryMenu;
		}
	}
	
	private void onConfirm(boolean result)
	{
		if(result && !menu.getMenuItem().isEmpty())
			PacketDistributor.sendToServer(new CaptchaDeckPackets.TriggerModusButton());
		minecraft.screen = this;
	}
	
}