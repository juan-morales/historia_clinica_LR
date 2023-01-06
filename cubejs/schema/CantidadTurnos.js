cube(`CantidadTurnos`, {
  sql: `SELECT 
          has.appointment_id as id, has.changed_state_date as fecha_consulta, cs.name as especialidad,
          concat_ws(', ', concat_ws(' ', p.last_name, p.other_last_names), CASE WHEN pex.name_self_determination IS NULL THEN concat_ws(' ', p.first_name, p.middle_names) ELSE pex.name_self_determination END) AS profesional
        FROM 
          historic_appointment_state has
          JOIN appointment ap ON (has.appointment_id = ap.id)
          JOIN appointment_assn apss ON (apss.appointment_id = ap.id)
          JOIN diary d ON (apss.diary_id = d.id)
          JOIN healthcare_professional hp ON (d.healthcare_professional_id = hp.id)
          JOIN person p ON (hp.person_id = p.id)
          JOIN person_extended pex ON (pex.person_id = p.id)
          JOIN clinical_specialty cs ON (d.clinical_specialty_id = cs.id)
    WHERE has.appointment_state_id = 2`,
  
  measures: {
    cantidad_turnos: {
        sql: `id`,
        type: `count`,
        title: `Cantidad de turnos`
    }
  },
  
  dimensions: {
    // Fecha consulta
    fecha_consulta: {
      sql: `fecha_consulta`,
      type: `time`,
      title: 'Fecha consulta',
    },
    // Especialidad
    especialidad: {
      sql: `especialidad`,
      type: `string`,
      title: 'especialidad',
    },
    // Profesional
    profesional: {
      sql: `profesional`,
      type: `string`,
      title: 'profesional',
    },
  },
  title:` `,
  dataSource: `default`
});
