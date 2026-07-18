use `ry-vue`;
set names utf8mb4;

-- 修复早期初始化时因客户端未声明 UTF-8 而产生的双重编码中文。
-- 仅处理包含典型乱码标记且能够无损还原的系统表字段，ASCII 与正常中文不会被修改。
drop procedure if exists repair_system_text_charset;

delimiter $$
create procedure repair_system_text_charset()
begin
    declare finished int default 0;
    declare current_table varchar(128);
    declare current_column varchar(128);

    declare text_columns cursor for
        select table_name, column_name
        from information_schema.columns
        where table_schema = database()
          and table_name like 'sys\_%'
          and data_type in ('char', 'varchar', 'tinytext', 'text', 'mediumtext', 'longtext');

    declare continue handler for not found set finished = 1;

    open text_columns;
    repair_loop: loop
        fetch text_columns into current_table, current_column;
        if finished = 1 then
            leave repair_loop;
        end if;

        set @repair_sql = concat(
            'update `', current_table, '` set `', current_column, '` = ',
            'convert(cast(convert(`', current_column, '` using latin1) as binary) using utf8mb4) ',
            'where `', current_column, '` is not null ',
            'and `', current_column, '` regexp ''[ÃÂâäåæçèé]'' ',
            'and convert(cast(convert(`', current_column, '` using latin1) as binary) using utf8mb4) is not null'
        );

        prepare repair_statement from @repair_sql;
        execute repair_statement;
        deallocate prepare repair_statement;
    end loop;
    close text_columns;
end$$
delimiter ;

call repair_system_text_charset();
drop procedure repair_system_text_charset;
