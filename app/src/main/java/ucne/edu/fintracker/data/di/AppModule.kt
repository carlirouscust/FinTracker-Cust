package ucne.edu.fintracker.data.di

import ucne.edu.fintracker.data.local.dao.CategoriaDao
import ucne.edu.fintracker.data.local.dao.LimiteGastoDao
import ucne.edu.fintracker.data.local.dao.MetaAhorroDao
import ucne.edu.fintracker.data.local.dao.PagoRecurrenteDao
import ucne.edu.fintracker.data.local.dao.TransaccionDao
import ucne.edu.fintracker.data.local.dao.UsuarioDao
import ucne.edu.fintracker.data.local.database.FinTrackerDb
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFinTrackerDb(@ApplicationContext context: Context): FinTrackerDb =
        Room.databaseBuilder(
            context,
            FinTrackerDb::class.java,
            "Fintracker.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUsuarioDao(db: FinTrackerDb): UsuarioDao = db.usuarioDao()

    @Provides
    @Singleton
    fun provideTransaccionDao(db: FinTrackerDb): TransaccionDao = db.transaccionDao()

    @Provides
    @Singleton
    fun providePagoRecurrenteDao(db: FinTrackerDb): PagoRecurrenteDao = db.pagoRecurrenteDao()

    @Provides
    @Singleton
    fun provideMetaAhorroDao(db: FinTrackerDb): MetaAhorroDao = db.metaAhorroDao()

    @Provides
    @Singleton
    fun provideLimiteGastoDao(db: FinTrackerDb): LimiteGastoDao = db.limiteGastoDao()

    @Provides
    @Singleton
    fun provideCategoriaDao(db: FinTrackerDb): CategoriaDao = db.categoriaDao()
}
