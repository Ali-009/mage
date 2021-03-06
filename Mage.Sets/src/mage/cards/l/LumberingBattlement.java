package mage.cards.l;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.common.delayed.OnLeaveReturnExiledToBattlefieldAbility;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateDelayedTriggeredAbilityEffect;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.keyword.VigilanceAbility;
import mage.cards.*;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.TokenPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.util.CardUtil;

import java.util.UUID;

import static mage.constants.Outcome.Benefit;

/**
 * @author TheElk801
 */
public final class LumberingBattlement extends CardImpl {

    public LumberingBattlement(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{4}{W}");

        this.subtype.add(SubType.BEAST);
        this.power = new MageInt(4);
        this.toughness = new MageInt(5);

        // Vigilance
        this.addAbility(VigilanceAbility.getInstance());

        // When Lumbering Battlement enters the battlefield, exile any number of other nontoken creatures you control until it leaves the battlefield.
        Ability ability = new EntersBattlefieldTriggeredAbility(new LumberingBattlementEffect());
        ability.addEffect(new CreateDelayedTriggeredAbilityEffect(new OnLeaveReturnExiledToBattlefieldAbility()));
        this.addAbility(ability);

        // Lumbering Battlement gets +2/+2 for each card exiled with it.
        this.addAbility(new SimpleStaticAbility(new BoostSourceEffect(
                LumberinBattlementValue.instance,
                LumberinBattlementValue.instance,
                Duration.WhileOnBattlefield
        ).setText("{this} gets +2/+2 for each card exiled with it.")));
    }

    private LumberingBattlement(final LumberingBattlement card) {
        super(card);
    }

    @Override
    public LumberingBattlement copy() {
        return new LumberingBattlement(this);
    }
}

class LumberingBattlementEffect extends OneShotEffect {

    private static final FilterPermanent filter
            = new FilterControlledCreaturePermanent("nontoken creatures");

    static {
        filter.add(Predicates.not(TokenPredicate.instance));
    }

    LumberingBattlementEffect() {
        super(Benefit);
        staticText = "exile any number of other nontoken creatures you control until it leaves the battlefield";
    }

    private LumberingBattlementEffect(final LumberingBattlementEffect effect) {
        super(effect);
    }

    @Override
    public LumberingBattlementEffect copy() {
        return new LumberingBattlementEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Permanent sourcePerm = source.getSourcePermanentIfItStillExists(game);
        if (player == null || sourcePerm == null) {
            return false;
        }
        Target target = new TargetPermanent(0, Integer.MAX_VALUE, filter, true);
        if (!player.choose(Outcome.Neutral, target, source.getSourceId(), game)) {
            return false;
        }
        Cards cards = new CardsImpl();
        for (UUID targetId : target.getTargets()) {
            Permanent permanent = game.getPermanent(targetId);
            if (permanent != null) {
                cards.add(permanent);
            }
        }
        return player.moveCardsToExile(
                cards.getCards(game), source, game, true,
                CardUtil.getExileZoneId(
                        game, source.getSourceId(), source.getSourceObjectZoneChangeCounter()
                ), sourcePerm.getIdName()
        );
    }
}

enum LumberinBattlementValue implements DynamicValue {
    instance;

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        int counter = 0;
        for (UUID cardId : game.getExile().getExileZone(CardUtil.getExileZoneId(
                game, sourceAbility.getSourceId(), sourceAbility.getSourceObjectZoneChangeCounter()
        ))) {
            Card card = game.getCard(cardId);
            if (card != null) {
                counter++;
            }
        }
        return 2 * counter;
    }

    @Override
    public DynamicValue copy() {
        return instance;
    }

    @Override
    public String getMessage() {
        return "";
    }
}