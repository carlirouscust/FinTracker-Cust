package ucne.edu.fintracker.data.mappers

import ucne.edu.fintracker.data.local.entity.CategoriaEntity
import ucne.edu.fintracker.data.local.entity.LimiteGastoEntity
import ucne.edu.fintracker.data.local.entity.MetaAhorroEntity
import ucne.edu.fintracker.data.local.entity.PagoRecurrenteEntity
import ucne.edu.fintracker.data.local.entity.TransaccionEntity
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto
import ucne.edu.fintracker.remote.dto.TransaccionDto

fun TransaccionDto.toEntity() = TransaccionEntity(
    transaccionId = this.transaccionId,
    monto = this.monto,
    categoriaId = this.categoriaId,
    fecha = this.fecha,
    notas = this.notas,
    tipo = this.tipo,
    usuarioId = this.usuarioId,
    syncPending = false
)

fun TransaccionEntity.toDto() = TransaccionDto(
    transaccionId = this.transaccionId,
    monto = this.monto,
    categoriaId = this.categoriaId,
    fecha = this.fecha,
    notas = this.notas,
    tipo = this.tipo,
    usuarioId = this.usuarioId
)
fun PagoRecurrenteDto.toEntity(syncPending: Boolean = false) = PagoRecurrenteEntity(
    pagoRecurrenteId = this.pagoRecurrenteId,
    monto = this.monto,
    categoriaId = this.categoriaId,
    frecuencia = this.frecuencia,
    fechaInicio = this.fechaInicio,
    fechaFin = this.fechaFin,
    activo = this.activo,
    usuarioId = this.usuarioId,
    syncPending = syncPending
)
fun PagoRecurrenteEntity.toDto() = PagoRecurrenteDto(
    pagoRecurrenteId = this.pagoRecurrenteId,
    monto = this.monto,
    categoriaId = this.categoriaId,
    frecuencia = this.frecuencia,
    fechaInicio = this.fechaInicio,
    fechaFin = this.fechaFin,
    activo = this.activo,
    usuarioId = this.usuarioId
)

fun MetaAhorroDto.toEntity(syncPending: Boolean = false) = MetaAhorroEntity(
    metaAhorroId = this.metaAhorroId,
    nombreMeta = this.nombreMeta,
    montoObjetivo = this.montoObjetivo,
    fechaFinalizacion = this.fechaFinalizacion,
    contribucionRecurrente = this.contribucionRecurrente,
    imagen = this.imagen,
    montoActual = this.montoActual,
    montoAhorrado = this.montoAhorrado,
    fechaMontoAhorrado = this.fechaMontoAhorrado,
    usuarioId = this.usuarioId,
    syncPending = syncPending
)

fun MetaAhorroEntity.toDto() = MetaAhorroDto(
    metaAhorroId = this.metaAhorroId,
    nombreMeta = this.nombreMeta,
    montoObjetivo = this.montoObjetivo,
    fechaFinalizacion = this.fechaFinalizacion,
    contribucionRecurrente = this.contribucionRecurrente,
    imagen = this.imagen,
    montoActual = this.montoActual,
    montoAhorrado = this.montoAhorrado,
    fechaMontoAhorrado = this.fechaMontoAhorrado,
    usuarioId = this.usuarioId
)

fun LimiteGastoDto.toEntity(syncPending: Boolean = false) = LimiteGastoEntity(
    limiteGastoId = this.limiteGastoId,
    montoLimite = this.montoLimite,
    periodo = this.periodo,
    categoriaId = this.categoriaId,
    usuarioId = this.usuarioId,
    syncPending = syncPending
)

fun LimiteGastoEntity.toDto() = LimiteGastoDto(
    limiteGastoId = this.limiteGastoId,
    montoLimite = this.montoLimite,
    periodo = this.periodo,
    categoriaId = this.categoriaId,
    usuarioId = this.usuarioId
)

fun CategoriaDto.toEntity(syncPending: Boolean = false): CategoriaEntity {
    return CategoriaEntity(
        categoriaId = this.categoriaId,
        nombre = this.nombre,
        tipo = this.tipo,
        icono = this.icono,
        colorFondo = this.colorFondo,
        usuarioId = this.usuarioId,
        syncPending = syncPending
    )
}

fun CategoriaEntity.toDto(): CategoriaDto {
    return CategoriaDto(
        categoriaId = this.categoriaId,
        nombre = this.nombre,
        tipo = this.tipo,
        icono = this.icono,
        colorFondo = this.colorFondo,
        usuarioId = this.usuarioId,
    )
}
