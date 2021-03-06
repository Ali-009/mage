
package mage.cards.v;

import mage.abilities.Ability;
import mage.abilities.costs.common.DiscardXTargetCost;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.FilterCard;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterAttackingCreature;
import mage.game.Game;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.targetadjustment.TargetAdjuster;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class VengefulDreams extends CardImpl {

    public VengefulDreams(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{W}{W}");

        // As an additional cost to cast Vengeful Dreams, discard X cards.
        this.getSpellAbility().addCost(new DiscardXTargetCost(new FilterCard("cards"), true));

        // Exile X target attacking creatures.
        Effect effect = new ExileTargetEffect();
        effect.setText("Exile X target attacking creatures");
        this.getSpellAbility().addEffect(effect);
        this.getSpellAbility().setTargetAdjuster(VengefulDreamsAdjuster.instance);
    }

    public VengefulDreams(final VengefulDreams card) {
        super(card);
    }

    @Override
    public VengefulDreams copy() {
        return new VengefulDreams(this);
    }
}

enum VengefulDreamsAdjuster implements TargetAdjuster {
    instance;
    private static final FilterPermanent filter = new FilterAttackingCreature();

    @Override
    public void adjustTargets(Ability ability, Game game) {
        Target target = new TargetPermanent(0, ability.getManaCostsToPay().getX(), filter, false);
        ability.addTarget(target);
    }
}