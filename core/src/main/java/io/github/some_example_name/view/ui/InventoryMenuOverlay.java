package io.github.some_example_name.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.controller.inventory.InventoryController;
import io.github.some_example_name.model.inventory.Charm;
import io.github.some_example_name.model.inventory.CharmType;
import io.github.some_example_name.util.L10n;

import java.util.EnumMap;
import java.util.Map;

public class InventoryMenuOverlay extends Table {
    private final Skin skin;
    private final InventoryController controller;
    private final Table charmListTable;
    private final Label notchLabel;
    private final Label messageLabel;
    private final Map<CharmType, Texture> charmTextures = new EnumMap<>(CharmType.class);
    private final Array<Texture> ownedTextures = new Array<>();

    public InventoryMenuOverlay(Skin skin, InventoryController controller) {
        super(skin);

        this.skin = skin;
        this.controller = controller;

        setFillParent(true);
        setVisible(false);
        setBackground(
            skin.newDrawable(
                "white",
                new Color(0f, 0f, 0f, 0.76f)
            )
        );

        loadCharmTextures();

        Table menuBox = new Table(skin);
        menuBox.setBackground(
            skin.newDrawable(
                "white",
                new Color(0.02f, 0.02f, 0.04f, 0.82f)
            )
        );
        menuBox.pad(24f);

        Label titleLabel = new Label(L10n.tr("Inventory"), skin);
        titleLabel.setFontScale(1.6f);

        notchLabel = new Label("", skin);
        messageLabel = new Label(
            L10n.tr("Click a charm to equip or unequip it."),
            skin
        );
        messageLabel.setColor(Color.LIGHT_GRAY);
        messageLabel.setWrap(true);

        charmListTable = new Table(skin);
        charmListTable.top();

        ScrollPane scrollPane = new ScrollPane(charmListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);

        menuBox.add(titleLabel).padBottom(10f).row();
        menuBox.add(notchLabel).padBottom(8f).row();
        menuBox.add(messageLabel).width(610f).padBottom(10f).row();
        menuBox.add(scrollPane).width(650f).height(430f).row();

        add(menuBox);
        refresh();
    }

    public void showMenu() {
        refresh();
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }

    public void toggleMenu() {
        if (isVisible()) hideMenu();
        else showMenu();
    }

    public void refresh() {
        notchLabel.setText(
            L10n.dynamic(
                "Charm Notches: "
                    + controller.getUsedNotches()
                    + " / "
                    + controller.getMaxNotches()
            )
        );

        charmListTable.clear();

        for (Charm charm : controller.getCharms()) {
            Table row = new Table(skin);

            Texture iconTexture = charmTextures.get(charm.getType());
            if (iconTexture != null) {
                Image icon = new Image(iconTexture);
                row.add(icon).size(54f).padRight(10f);
            }

            Table textColumn = new Table(skin);
            TextButton charmButton = new TextButton(
                L10n.charmText(charm),
                skin
            );

            charmButton.setDisabled(!charm.isUnlocked());

            charmButton.addListener(
                new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                    @Override
                    public void changed(
                        ChangeEvent event,
                        com.badlogic.gdx.scenes.scene2d.Actor actor
                    ) {
                        if (!charm.isUnlocked()) {
                            messageLabel.setColor(Color.SCARLET);
                            messageLabel.setText(
                                L10n.tr("This charm is hidden in a secret room.")
                            );
                            return;
                        }

                        boolean changed = controller.toggleCharm(charm.getType());

                        if (changed) {
                            messageLabel.setColor(Color.LIGHT_GRAY);
                            messageLabel.setText(
                                L10n.charmDescription(charm.getType())
                            );
                        } else {
                            messageLabel.setColor(Color.SCARLET);
                            messageLabel.setText(
                                L10n.tr("Not enough charm notches.")
                            );
                        }

                        refresh();
                    }
                }
            );

            Label description = new Label(
                charm.isUnlocked()
                    ? L10n.charmDescription(charm.getType())
                    : L10n.tr("Find this charm in a secret room."),
                skin
            );
            description.setColor(Color.GRAY);
            description.setWrap(true);
            description.setFontScale(0.82f);

            textColumn.add(charmButton)
                .width(520f)
                .height(42f)
                .row();

            textColumn.add(description)
                .width(520f)
                .padTop(4f)
                .row();

            row.add(textColumn);

            charmListTable.add(row)
                .width(610f)
                .pad(5f)
                .row();
        }
    }

    private void loadCharmTextures() {
        loadCharmTexture(
            CharmType.UNBREAKABLE_STRENGTH,
            "ui/charms/unbreakable_strength.png"
        );
        loadCharmTexture(
            CharmType.QUICK_FOCUS,
            "ui/charms/quick_focus.png"
        );
        loadCharmTexture(
            CharmType.HEAVY_BLOW,
            "ui/charms/heavy_blow.png"
        );
        loadCharmTexture(
            CharmType.SHARP_SHADOW,
            "ui/charms/sharp_shadow.png"
        );
        loadCharmTexture(
            CharmType.VOID_HEART,
            "ui/charms/void_heart.png"
        );
    }

    private void loadCharmTexture(CharmType type, String path) {
        if (!Gdx.files.internal(path).exists()) {
            return;
        }

        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );
        charmTextures.put(type, texture);
        ownedTextures.add(texture);
    }

    public void dispose() {
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
        ownedTextures.clear();
        charmTextures.clear();
    }
}
