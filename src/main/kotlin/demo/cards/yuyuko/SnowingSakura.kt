package demo.cards.yuyuko

import basemod.abstracts.CustomCard
import com.megacrit.cardcrawl.actions.AbstractGameAction
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction
import com.megacrit.cardcrawl.actions.common.GainEnergyAction
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction
import com.megacrit.cardcrawl.actions.common.ReducePowerAction
import com.megacrit.cardcrawl.cards.AbstractCard
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.monsters.AbstractMonster
import com.megacrit.cardcrawl.ui.panels.EnergyPanel
import demo.cards.isSakura
import demo.patches.CardColorEnum
import demo.powers.DiaphaneityPower
import demo.powers.FanPower

class SnowingSakura : CustomCard(
        ID, NAME, IMAGE_PATH, COST, DESCRIPTION,
        CardType.SKILL, CardColorEnum.YUYUKO_COLOR,
        CardRarity.RARE, CardTarget.SELF
) {
    companion object {
        @JvmStatic
        val ID = "Snowing Sakura"
        val IMAGE_PATH = "images/yuyuko/cards/skill3.png"
        val COST = 0
        private val CARD_STRINGS = CardCrawlGame.languagePack.getCardStrings(ID)
        val NAME = CARD_STRINGS.NAME!!
        val DESCRIPTION = CARD_STRINGS.DESCRIPTION!!
        val UPGRADE_DESCRIPTION = CARD_STRINGS.UPGRADE_DESCRIPTION!!
    }

    init {
        this.exhaust = true
    }

    override fun makeCopy(): AbstractCard = SnowingSakura()

    override fun use(self: AbstractPlayer?, target: AbstractMonster?) {
        AbstractDungeon.actionManager.addToBottom(
                ReducePowerAction(
                        self, self,
                        FanPower.POWER_ID,
                        1
                )
        )
        var count = countSakura()
        AbstractDungeon.actionManager.addToBottom(
                ApplyPowerAction(
                        self, self,
                        DiaphaneityPower(self!!, count),
                        count
                )
        )
        count += self.getPower(DiaphaneityPower.POWER_ID)?.amount ?: 0
        AbstractDungeon.actionManager.addToBottom(
                GainEnergyAction(count / 5)
        )
        count = count / 5 + EnergyPanel.totalCount
        AbstractDungeon.actionManager.addToBottom(
                MakeTempCardInDrawPileAction(SakuraBloom(), count / 3, true, true)
        )
        if (upgraded) {
            degrade()
        }
    }


    private fun countSakura(): Int {
        val player = AbstractDungeon.player
        val groups = listOf(player.hand, player.drawPile, player.discardPile)
        return groups
                .map {
                    it.group.filter { it.isSakura() }.size
                }
                .reduce { acc, i -> acc + i }
    }

    private fun degrade() {
        this.upgraded = false
        this.name = NAME
        this.timesUpgraded = 0
        this.rawDescription = DESCRIPTION
        AbstractDungeon.actionManager.addToBottom(
                object : AbstractGameAction() {
                    override fun update() {
                        this.isDone = true
                        this@SnowingSakura.exhaust = true
                    }
                }
        )
        this.initializeDescription()
        this.initializeTitle()
    }


    override fun upgrade() {
        if (!this.upgraded) {
            upgradeName()
            this.exhaust = false
            this.rawDescription = UPGRADE_DESCRIPTION
            this.initializeDescription()
        }
    }

}