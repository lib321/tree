package alibek.tree;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class TreeApplication {

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
            System.out.print("Введите номер категории в которую необходимо добавить новую: ");
            Scanner scanner = new Scanner(System.in);
            String id = scanner.nextLine();
            long id1 = Integer.parseInt(id);
            if (id1 == 0) {
                TypedQuery<Integer> treeEntityTypedQuery1 = manager.createQuery("select max(t.rightKey) from TreeEntity t", Integer.class);
                Integer treeMaxRightKey = treeEntityTypedQuery1.getSingleResult();
                TreeEntity treeEntity = new TreeEntity();
                System.out.print("Введите название новой категории: ");
                String newCategoryName = scanner.nextLine();
                treeEntity.setName(newCategoryName);
                treeEntity.setLeftKey(treeMaxRightKey + 1);
                treeEntity.setRightKey(treeEntity.getLeftKey() + 1);
                treeEntity.setLevel(0);
                manager.persist(treeEntity);
            } else {
                TreeEntity treeEntity = manager.find(TreeEntity.class, id1);
                Query updateLeftKeysQuery = manager.createQuery(
                        "update TreeEntity t set t.leftKey = t.leftKey + 2 where t.leftKey > ?1"
                );
                updateLeftKeysQuery.setParameter(1, treeEntity.getRightKey());
                updateLeftKeysQuery.executeUpdate();
                Query updateRightKeysQuery = manager.createQuery(
                        "update TreeEntity t set t.rightKey = t.rightKey + 2 where t.rightKey >= ?1"
                );
                updateRightKeysQuery.setParameter(1, treeEntity.getRightKey());
                updateRightKeysQuery.executeUpdate();
                System.out.print("Введите название новой категории: ");
                String newCategoryName = scanner.nextLine();
                TreeEntity newTreeEntity = new TreeEntity();
                newTreeEntity.setName(newCategoryName);
                newTreeEntity.setLeftKey(treeEntity.getRightKey());
                newTreeEntity.setRightKey(treeEntity.getRightKey() + 1);
                newTreeEntity.setLevel(treeEntity.getLevel() + 1);
                manager.persist(newTreeEntity);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
