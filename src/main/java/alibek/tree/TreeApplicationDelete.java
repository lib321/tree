package alibek.tree;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class TreeApplicationDelete {

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        TypedQuery<TreeEntity> treeEntityTypedQuery = manager.createQuery("select t from TreeEntity t order by t.leftKey", TreeEntity.class);
        List<TreeEntity> treeEntities = treeEntityTypedQuery.getResultList();

        for (TreeEntity treeEntity : treeEntities) {
            System.out.println("- ".repeat(treeEntity.getLevel()) + treeEntity.getName());
        }

        try {
            manager.getTransaction().begin();
            System.out.print("Введите номер категории которую необходимо удалить: ");
            Scanner scanner = new Scanner(System.in);
            String id = scanner.nextLine();
            long id1 = Integer.parseInt(id);
            TreeEntity treeEntity = manager.find(TreeEntity.class, id1);
            Query deleteCategory = manager.createQuery(
                    "delete from TreeEntity where leftKey >= ?1 and rightKey <= ?2"
            );
            deleteCategory.setParameter(1, treeEntity.getLeftKey());
            deleteCategory.setParameter(2, treeEntity.getRightKey());
            deleteCategory.executeUpdate();
            Query updateLeftkeysQuery = manager.createQuery(
                    "update TreeEntity t set t.leftKey = t.leftKey - (?2 - ?3 + 1) where t.leftKey > ?1"
            );
            updateLeftkeysQuery.setParameter(1, treeEntity.getRightKey());
            updateLeftkeysQuery.setParameter(2, treeEntity.getRightKey());
            updateLeftkeysQuery.setParameter(3, treeEntity.getLeftKey());
            updateLeftkeysQuery.executeUpdate();
            Query updateRightkeysQuery = manager.createQuery(
                    "update TreeEntity t set t.rightKey = t.rightKey - (?2 - ?3 + 1) where t.rightKey >= ?1"
            );
            updateRightkeysQuery.setParameter(1, treeEntity.getRightKey());
            updateRightkeysQuery.setParameter(2, treeEntity.getRightKey());
            updateRightkeysQuery.setParameter(3, treeEntity.getLeftKey());
            updateRightkeysQuery.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
