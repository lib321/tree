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
        System.out.println("[1] - Создать новую категорию");
        System.out.println("[2] - Переместить категорию");
        System.out.println("[3] - Удалить категорию");
        System.out.print("Выберите действие: ");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        try {
            manager.getTransaction().begin();
            switch (str) {
                case "1" -> {
                    System.out.print("Введите номер категории в которую необходимо добавить новую: ");
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
                }
                case "2" -> {
                    System.out.print("Введите номер категории которую необходимо переместить: ");
                    String id = scanner.nextLine();
                    long id1 = Integer.parseInt(id);
                    TreeEntity treeEntity = manager.find(TreeEntity.class, id1);
                    Query negativeLeftKeys = manager.createQuery(
                            "update TreeEntity t set t.leftKey = -t.leftKey where t.leftKey >= ?1 and t.leftKey < ?2"
                    );
                    negativeLeftKeys.setParameter(1, treeEntity.getLeftKey());
                    negativeLeftKeys.setParameter(2, treeEntity.getRightKey());
                    negativeLeftKeys.executeUpdate();
                    Query negativeRightKeys = manager.createQuery(
                            "update TreeEntity t set t.rightKey = -t.rightKey where t.rightKey > ?1 and t.rightKey <= ?2"
                    );
                    negativeRightKeys.setParameter(1, treeEntity.getLeftKey());
                    negativeRightKeys.setParameter(2, treeEntity.getRightKey());
                    negativeRightKeys.executeUpdate();
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
                    System.out.print("Введите номер категории куда необходимо переместить: ");
                    String moveId = scanner.nextLine();
                    long moveId1 = Integer.parseInt(moveId);
                    if (moveId1 == 0) {
                        TypedQuery<Integer> getMaxRightKey = manager.createQuery(
                                "select max (t.rightKey) from TreeEntity t", Integer.class
                        );
                        Integer maxRightKey = getMaxRightKey.getSingleResult();
                        Query updateTreeEntity = manager.createQuery("""
                        update TreeEntity t
                        set t.leftKey = 0 - t.leftKey - ?1 + ?2 + 1,
                            t.rightKey = 0 - t.rightKey - ?1 + ?2 + 1,
                            t.level = t.level - ?3
                        where t.leftKey < 0
                        """
                        );
                        updateTreeEntity.setParameter(1, treeEntity.getLeftKey());
                        updateTreeEntity.setParameter(2, maxRightKey);
                        updateTreeEntity.setParameter(3, treeEntity.getLevel());
                        updateTreeEntity.executeUpdate();
                    } else {
                        TreeEntity moveTreeEntity = manager.find(TreeEntity.class, moveId1);
                        manager.refresh(moveTreeEntity);

                        Query moveLeftKeys = manager.createQuery(
                                "update TreeEntity t set t.leftKey = t.leftKey + (?1 - ?2 + 1) where t.leftKey > ?3"
                        );
                        moveLeftKeys.setParameter(1, treeEntity.getRightKey());
                        moveLeftKeys.setParameter(2, treeEntity.getLeftKey());
                        moveLeftKeys.setParameter(3, moveTreeEntity.getRightKey());
                        moveLeftKeys.executeUpdate();
                        Query moveRightKeys = manager.createQuery(
                                "update TreeEntity t set t.rightKey = t.rightKey + (?1 - ?2 + 1) where t.rightKey >= ?3"
                        );
                        moveRightKeys.setParameter(1, treeEntity.getRightKey());
                        moveRightKeys.setParameter(2, treeEntity.getLeftKey());
                        moveRightKeys.setParameter(3, moveTreeEntity.getRightKey());
                        moveRightKeys.executeUpdate();

                        manager.refresh(moveTreeEntity);

                        Query updateKeysLevel = manager.createQuery("""
                        update TreeEntity t
                        set t.leftKey = 0 - (t.leftKey) + (?1 - ?2 - 1),
                            t.rightKey = 0 - (t.rightKey) + (?1 - ?2 - 1),
                            t.level = t.level - ?3 + ?4 + 1
                        where t.leftKey < 0
                        """
                        );
                        updateKeysLevel.setParameter(1, moveTreeEntity.getRightKey());
                        updateKeysLevel.setParameter(2, treeEntity.getRightKey());
                        updateKeysLevel.setParameter(3, treeEntity.getLevel());
                        updateKeysLevel.setParameter(4, moveTreeEntity.getLevel());
                        updateKeysLevel.executeUpdate();
                    }
                    manager.getTransaction().commit();
                }
                case "3" -> {
                    System.out.print("Введите номер категории которую необходимо удалить: ");
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
                }
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
