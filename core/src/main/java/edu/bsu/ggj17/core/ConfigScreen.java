package edu.bsu.ggj17.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import playn.core.Game;
import react.Connection;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Colors;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.List;

public class ConfigScreen extends ScreenStack.UIScreen {

    public final UnitSignal done = new UnitSignal();
    private final FlappyPitchGame game;

    public ConfigScreen(FlappyPitchGame game) {
        super(game.plat);
        this.game = Preconditions.checkNotNull(game);

        Root root = iface.createRoot(AxisLayout.vertical(), GameStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);

        root.add(new Label("MIXER").setStyles(Style.COLOR.is(Colors.WHITE)));
        root.add(createMixerSelectionGroup());
        root.add(new StatusLabel());

        root.add(new Button("Back").onClick(new UnitSlot() {
            @Override
            public void onEmit() {
                done.emit();
            }
        }));
    }

    private Element<?> createMixerSelectionGroup() {
        Selector selector = new Selector().preventDeselection();
        selector.selected.connect(new Slot<Element<?>>() {
            @Override
            public void onEmit(Element<?> element) {
                if (element!=null) {
                    game.mixer.update(AudioSystem.getMixer(((MixerCheckBox) element).mixerInfo));
                } else {
                    game.mixer.update(null);
                }
            }
        });

        Group mixerGroup = new Group(new TableLayout(TableLayout.COL.stretch(), TableLayout.COL.alignLeft()).gaps(6, 6));
        for (Mixer.Info mixerInfo : makeMixerList()) {
            MixerCheckBox button = new MixerCheckBox(mixerInfo);
            mixerGroup.add(button,
                    new Label(mixerInfo.getName()).setStyles(Style.COLOR.is(Colors.WHITE)));
        }
        selector.add(mixerGroup);

        return mixerGroup;
    }

    private List<Mixer.Info> makeMixerList() {
        final Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        List<Mixer.Info> list = Lists.newArrayList();
        for (final Mixer.Info mixerInfo : mixers) {
            if (AudioSystem.getMixer(mixerInfo).getTargetLineInfo().length != 0) {
                list.add(mixerInfo);
            }
        }
        return list;
    }

    @Override
    public Game game() {
        return game;
    }

    private final class MixerCheckBox extends CheckBox {
        public final Mixer.Info mixerInfo;

        public MixerCheckBox(Mixer.Info mixerInfo) {
            this.mixerInfo = Preconditions.checkNotNull(mixerInfo);
        }
    }

    private final class StatusLabel extends Label {
        private final String noAudioMessage = "No audio detected";
        private Connection connection = null;

        public StatusLabel() {
            setStyles(Style.COLOR.is(Colors.WHITE));
            game.mixer.connect(new Slot<Mixer>() {
                @Override
                public void onEmit(Mixer mixer) {
                    mixerChanged();
                }
            });
            mixerChanged();
        }

        private void mixerChanged() {
            setText(noAudioMessage);
            connection = game.pitch.connect(new Slot<Float>() {
                @Override
                public void onEmit(Float aFloat) {
                    if (aFloat != null) {
                        setText("This mixer should work!");
                        connection.close();
                    }
                }
            });
        }
    }
}

