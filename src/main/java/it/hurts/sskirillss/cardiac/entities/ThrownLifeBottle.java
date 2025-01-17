package it.hurts.sskirillss.cardiac.entities;

import it.hurts.sskirillss.cardiac.init.EntityRegistry;
import it.hurts.sskirillss.cardiac.init.ItemRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class ThrownLifeBottle extends ThrowableItemProjectile {
    public ThrownLifeBottle(EntityType<? extends ThrownLifeBottle> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.LIFE_BOTTLE.get();
    }

    @Override
    protected float getGravity() {
        return 0.07F;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (this.level() instanceof ServerLevel) {
            this.level().levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.HEALING));

            int steps = 5 + random.nextInt(10);

            for (int i = 0; i < steps; i++) {
                LifeOrb orb = new LifeOrb(EntityRegistry.LIFE_ORB.get(), this.level());

                orb.setLife(random.nextFloat() * (10F / steps));
                orb.setPos(Vec3.atCenterOf(this.blockPosition()));
                orb.setDeltaMovement(
                        (-1 + 2 * random.nextFloat()) * 0.15F,
                        0.1F + random.nextFloat() * 0.2F,
                        (-1 + 2 * random.nextFloat()) * 0.15F
                );

                this.level().addFreshEntity(orb);
            }

            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}