/*
    Copyright(c) 2021 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ElementOrder;
import com.djrapitops.plan.extension.Group;
import com.djrapitops.plan.extension.annotation.DataBuilderProvider;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TabInfo;
import com.djrapitops.plan.extension.builder.ExtensionDataBuilder;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import org.bukkit.OfflinePlayer;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.kingdom.Nation;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.player.KingdomPlayer;

import java.util.*;

/**
 * DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "KingdomsX", iconName = "fort-awesome", iconFamily = Family.BRAND, color = Color.AMBER)
@TabInfo(tab = "Donations", iconName = "money-bill-wave", elementOrder = {ElementOrder.VALUES})
public class KingdomsXExtension implements DataExtension {

    public KingdomsXExtension() { }

    @DataBuilderProvider
    public ExtensionDataBuilder playerData(UUID playerUUID) {
        KingdomPlayer player = KingdomPlayer.getKingdomPlayer(playerUUID);
        boolean hasKingdom = player.hasKingdom();

        Optional<Kingdom> kingdomOptional = Optional.ofNullable(player.getKingdom());

        String kingdomName = kingdomOptional.map(Kingdom::getName).orElse(null);
        String kingName = kingdomOptional.map(Kingdom::getKing)
                .map(KingdomPlayer::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .orElse(null);
        String nationName = kingdomOptional.map(Kingdom::getNation)
                .map(Nation::getName)
                .orElse(null);

        double power = player.getPower();
        long lastDonationTime = player.getLastDonationTime();
        long lastDonationAmount = player.getLastDonationAmount();
        long totalDonations = player.getTotalDonations();

        Icon fortIcon = Icon.called("fort-awesome").of(Family.BRAND).of(Color.AMBER).build();
        return newExtensionDataBuilder()
                .addValue(Boolean.class, valueBuilder("Has kingdom")
                        .priority(100)
                        .icon(fortIcon)
                        .buildBoolean(hasKingdom))
                .addValue(String[].class, valueBuilder("Kingdom")
                        .priority(90)
                        .icon(fortIcon)
                        .showInPlayerTable()
                        .buildGroup(new String[]{kingdomName}))
                .addValue(String.class, valueBuilder("King")
                        .priority(80)
                        .icon(Icon.called("chess-king").of(Color.AMBER).build())
                        .showAsPlayerPageLink()
                        .buildString(kingName))
                .addValue(String[].class, valueBuilder("Nation")
                        .priority(70)
                        .icon(Icon.called("flag").of(Color.GREEN).build())
                        .showInPlayerTable()
                        .buildGroup(new String[]{nationName != null ? nationName : "No nation"}))
                .addValue(Double.class, valueBuilder("Power")
                        .priority(60)
                        .icon(Icon.called("bolt").of(Color.AMBER).build())
                        .buildDouble(power))
                .addValue(Long.class, valueBuilder("Last Donation")
                        .priority(60)
                        .formatAsDateWithYear()
                        .icon(Icon.called("calendar").of(Family.REGULAR).of(Color.BLUE).build())
                        .showOnTab("Donations")
                        .buildNumber(lastDonationTime))
                .addValue(Long.class, valueBuilder("Last Donation Amount")
                        .priority(50)
                        .icon(Icon.called("money-bill-wave").of(Color.BLUE).build())
                        .showOnTab("Donations")
                        .buildNumber(lastDonationAmount))
                .addValue(Long.class, valueBuilder("Total Donations")
                        .priority(40)
                        .icon(Icon.called("money-bill-wave").of(Color.BLUE).build())
                        .showOnTab("Donations")
                        .buildNumber(totalDonations));
    }

    @DataBuilderProvider
    public ExtensionDataBuilder kingdomData(Group kingdomGroup) {
        Kingdom kingdom = Kingdom.getKingdom(kingdomGroup.getGroupName());

        Optional<Kingdom> kingdomOptional = Optional.ofNullable(kingdom);
        Set<SimpleChunkLocation> locations = kingdomOptional.map(Kingdom::getLandLocations)
                .orElseGet(HashSet::new);
        Long lastInvasion = kingdomOptional.map(Kingdom::getLastInvasion).orElse(null);
        List<Land> lands = kingdomOptional.map(Kingdom::getLands).orElseGet(ArrayList::new);
        Double might = kingdomOptional.map(Kingdom::getMight).orElse(null);

        Table.Factory locationTable = Table.builder()
                .columnOne("Location", Icon.called("map-pin").build());

        for (SimpleChunkLocation location : locations) {
            locationTable.addRow(location.getWorld() + ", x: " + location.getX() + " z:" + location.getZ());
        }

        return newExtensionDataBuilder()
                .addTable("locations", locationTable.build(), Color.GREEN)
                .addValue(Long.class, valueBuilder("Last invasion")
                        .priority(80)
                        .icon(Icon.called("calendar").of(Family.REGULAR).of(Color.RED).build())
                        .formatAsDateWithYear()
                        .buildNumber(lastInvasion))
                .addValue(Long.class, valueBuilder("Amount of lands")
                        .priority(70)
                        .icon(Icon.called("square").of(Color.AMBER).build())
                        .buildNumber(lands.size()))
                .addValue(Double.class, valueBuilder("Might")
                        .priority(60)
                        .icon(Icon.called("bolt").of(Color.BLUE).build())
                        .buildDouble(might));
    }
}