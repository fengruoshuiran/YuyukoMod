package yuyuko.powers

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction
import com.megacrit.cardcrawl.actions.utility.UseCardAction
import com.megacrit.cardcrawl.cards.AbstractCard
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.ImageMaster
import com.megacrit.cardcrawl.powers.AbstractPower
import yuyuko.event.ApplyDiaphaneityPowerEvent
import yuyuko.event.ApplyDiaphaneityPowerEvent.ApplyDiaphaneityPower.EFFECT
import yuyuko.event.EventDispenser
import kotlin.math.max
import kotlin.math.min

class NihilityPower(amount: Int) : AbstractPower() {

    companion object {
        @JvmStatic
        val POWER_ID = "Nihility"
        private val POWER_STRINGS = CardCrawlGame.languagePack.getPowerStrings(POWER_ID)
        val NAME = POWER_STRINGS.NAME!!
        val DESCRIPTIONS = POWER_STRINGS.DESCRIPTIONS!!
    }

    init {
        this.name = NAME
        this.ID = POWER_ID
        this.owner = AbstractDungeon.player
        this.amount = min(max(amount, 0), 999)
        this.updateDescription()
        this.type = PowerType.BUFF
        this.isTurnBased = true
        this.img = ImageMaster.loadImage("images/powers/nihility.png")
    }

    override fun onUseCard(card: AbstractCard?, action: UseCardAction?) {
        val monsters = AbstractDungeon.getCurrRoom().monsters.monsters
                .filter { !it.isDeadOrEscaped }
        val targets = listOf(AbstractDungeon.player, *monsters.toTypedArray())

        targets.forEach {
            EventDispenser.emit(ApplyDiaphaneityPowerEvent(it, owner, amount, EFFECT))
        }

    }

    override fun atEndOfTurn(isPlayer: Boolean) {
        if (isPlayer) {
            AbstractDungeon.actionManager.addToBottom(
                    RemoveSpecificPowerAction(owner, owner, this)
            )
        }
    }

    override fun updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]
    }

}
